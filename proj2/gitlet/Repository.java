package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    private static String userName = "user";
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    // 对象目录
    public static final File OBJECT_DIR = join(GITLET_DIR, "object");
    public static final File COMMIT_DIR = join(OBJECT_DIR, "commit");
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    // 缓存目录
    public static final File BUFFER_DIR = join(GITLET_DIR, "buffer");
    // 分支目录
    public static final File BRANCH_DIR = join(GITLET_DIR, "branch");
    // 未追踪文件目录
    public static final File UNSTACKED_DIR = join(GITLET_DIR, "unstacked");

    public static void init() {
        // 创建目录
        if (!GITLET_DIR.mkdir()) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
            return;
        }
        if (OBJECT_DIR.mkdir()) {
            COMMIT_DIR.mkdir();
            BLOB_DIR.mkdir();

        }
        BUFFER_DIR.mkdir();
        BRANCH_DIR.mkdir();
        UNSTACKED_DIR.mkdir();
        // 初始化用户名，待完善
        // 用户名定义移出方法
        // 下次调用gitlet，用户名不会保存，值为null
        // 初始化各部件
        // 初始提交
        // 获取当前时间戳new Date().getTime();
        Commit initCommit = new Commit("initial commit", userName, 0, " ", " ", 0, null);
        initCommit.save();
        // 初始化分支，将当前提交纳入初始化分支，初始分支为master
        // 分支类具有HEAD属性，存放initCommit hash， 此时 HEAD = initCommit.getSha1
        // 分支类也具有currentBranch属性，存放当前分支路线的名称，此时currentBranch = "master"
        // 构造时自动序列化进硬盘, 通过Utils.join(Repository.BRANCH_DIR, "branch")地址重新反序列化操作
        Branch branch = new Branch(initCommit.getSha1());
        // 初始化缓存区
        BufferAdd bufferadd = new BufferAdd();
        BufferRm bufferrm = new BufferRm();
        // 初始化未追踪文件
        UnStacked unStackedFiles = new UnStacked();
        // 存盘
        branch.save();
        bufferadd.save();
        bufferrm.save();
        unStackedFiles.save();
    }


    public static void add(String fileName) {
        // 写入要添加的文件地址
        File addFile = join(CWD, fileName);

        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        // 反序列化缓冲区(Add)/(Rm)
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        BufferRm bufferRm = BufferRm.readFromFile();
        // 反序列化未追踪文件
        UnStacked unStackedFiles = UnStacked.readFromFile();
        // 检查此次添加是否为恢复操作
        if (bufferRm.contain(addFile.getName())) {
            // 从Rm缓存区中删除对应文件
            bufferRm.rmFile(addFile.getName());
            bufferRm.save();
            return;
        }
        // 检查文件是否一致
        Branch curBranch = Branch.readFromFile();
        // 获取当前head ————操作好麻烦……
        String head = curBranch.getHEAD();
        // 读入当前commit
        Commit curCommit = Commit.readFromFile(head);
        // 检查是否包含当前文件，且内容一致 ———— 无操作
        if (curCommit.containStacked(fileName) && curCommit.isWithStackedSame(addFile)) {
            return;
        }
        // 写入缓冲区
        byte[] contents = readContents(addFile);
        bufferAdd.add(addFile.getName(), contents);
        unStackedFiles.rmUnStackedOfBufferAdd(bufferAdd);
        // 再次序列化缓冲区
        bufferAdd.save();
    }

    // commit辅助方法

    private static boolean isBlankMessage(String message) {
        return message.isEmpty() || message.trim().isEmpty();
    }

    private static Map<String, String> updateStacked(Commit current, BufferAdd bufferAdd,
                                                     BufferRm bufferRm) {
        // stacked (Map<File, String> Map-> fileName, BlobHash)
        // buffer (Map<File, byte[]>) create-> Blob
        Map<String, String> updatedStacked = current.getStackedBlob();
        // 更新修改的文件或新建的文件的追踪列表，需要创建Blob实例并序列化写入硬盘
        for (String key : bufferAdd) {
            Blob tempBlob = new Blob(key, bufferAdd.getContents(key));
            tempBlob.save();
            updatedStacked.put(key, tempBlob.getSha1());
        }
        // 更新删除的文件追踪列表，无需创建新的Blob实例也无需序列化写入硬盘
        for (String key : bufferRm) {
            // 这只是提交的更新追踪列表的方法，如删除的文件未被追踪并未缓存的检测在remove方法中实现
            updatedStacked.remove(key);
        }
        // 更新完毕清空缓存
        bufferAdd.clear();
        bufferRm.clear();

        return updatedStacked;
    }

    public static void commit(String message, String firstCommitID, String secondCommitID) {
        // 缓存区文件载入
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        BufferRm bufferRm = BufferRm.readFromFile();
        // 差错控制
        if (bufferAdd.isEmpty() & bufferRm.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (isBlankMessage(message)) {
            System.out.println("Please enter a commit message.");
            return;
        }
        // 分支文件载入
        /* branch:
                branch (Map<String, String> branchName, commitHash)
                HEAD (String -> commitHash)Commit
                currentBranch (String -> branchName)
        */
        Branch branch = Branch.readFromFile();
        String head = branch.getHEAD();
        // 当前commit -> Head 载入
        Commit currentCommit = Commit.readFromFile(head);
        // 更新追踪列表, (拷贝main父提交, 根据bufferAdd，bufferRm修改夫提交内容计入当前提交的追踪)
        Map<String, String> stacked = updateStacked(currentCommit, bufferAdd, bufferRm);
        // 新建commit实例
        int lastDepth = currentCommit.getDepth();
        Commit newCommit = null;
        if (firstCommitID == null && secondCommitID == null) {
            newCommit = new Commit(message, userName, new Date().getTime(),
                    head, " ", lastDepth, stacked);
        } else {
            newCommit = new Commit(message, userName, new Date().getTime(),
                    firstCommitID, secondCommitID, lastDepth, stacked);
        }
        head = newCommit.getSha1();
        // 重新设置到当前分支当前指针上
        branch.setHEAD(head);
        branch.updateCurBranch(head);
        /* 各部件依此重新序列化(谁被更改谁序列化)
            1.缓冲区状态被清空，遂需要再次序列化更新状态
            2.分支中HEAD状态被修改，遂需要再次序列化以更新状态
        */
        newCommit.save();
        bufferAdd.save();
        bufferRm.save();
        branch.save();
    }

    public static void remove(String filename) {
        File fileName = join(CWD, filename);

        /*  1.先查缓存区(add)，有的话从缓存区(add)中删除
            2.再查当前分支当前提交的追踪列表，有的话先存入缓存区(rm)，如果此文件在工作目录中，删除工作目录中的目标文件;
              在此方法中不急着更新追踪列表，因为更新追踪列表的状态统一集中到commit方法
            3.如果两种情况不满足进行差错控制
        */
        // 情况1
        // 缓存区文件载入
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        BufferRm bufferRm = BufferRm.readFromFile();
        int flag = 0;
        if (bufferAdd.contain(fileName.getName())) {
            bufferAdd.remove(fileName.getName());
            flag += 1;
        }
        // 情况2
        // 分支文件载入, 分支文件只做查看操作无需更新状态
        Branch branch = Branch.readFromFile();
        String head = branch.getHEAD();
        // 当前分支当前指向的commit载入
        Commit currentCommit = Commit.readFromFile(head);
        if (currentCommit.containStacked(fileName.getName())) {
            fileName.delete();
            bufferRm.add(fileName.getName());
            flag += 1;
        }
        if (flag == 0) {
            System.out.println("No reason to remove the file.");
            return;
        }
        // 缓冲区再次序列化
        bufferAdd.save();
        bufferRm.save();
    }

    // log()辅助方法，帮助实现dfs
    private static void logHelper(String point) {
        if (point.equals(" ")) {
            return;
        }
        // commit载入
        Commit currentCommit = Commit.readFromFile(point);
        System.out.println(currentCommit.toString());
        logHelper(currentCommit.getLast());
    }

    public static void log() {

        // 当前分支载入
        Branch branch = Branch.readFromFile();
        String head = branch.getHEAD();

        logHelper(head);
    }

    // 全局log
    public static void globalLog() {
        List<String> commitList;
        commitList = plainFilenamesIn(COMMIT_DIR);
        if (commitList != null) {
            for (String dir : commitList) {
                // 载入
                Commit commitPrintLog = Commit.readFromFile(dir);
                System.out.println(commitPrintLog.toString());
            }
        }
    }

    public static void find(String message) {
        // 记录次数
        int next = 0;
        List<String> commitList;
        commitList = plainFilenamesIn(COMMIT_DIR);
        if (commitList != null) {
            for (String dir : commitList) {
                Commit commitPrintID = Commit.readFromFile(dir);
                if (commitPrintID.isEqualsMessage(message)) {
                    System.out.println(commitPrintID.getSha1());
                    next += 1;
                }
            }
        }
        if (next == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        // 操作先载入
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        BufferRm bufferRm = BufferRm.readFromFile();
        Branch branch = Branch.readFromFile();
        if (bufferAdd == null || bufferRm == null || branch == null) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

        // 打印状态
        branch.showCurrentBranch();
        bufferAdd.showContain();
        bufferRm.showContain();
        // 保留标题
        String modify = "=== Modifications Not Staged For Commit ===\n\n";
        String unStacked = "=== Untracked Files ===\n\n";
        System.out.println(modify + unStacked);
        // 打印未追踪状态=
    }

    public static void checkoutFile(String fileName) {
        // 载入到内存
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        File fileDir = join(CWD, fileName);
        Branch branch = Branch.readFromFile();
        String head = branch.getHEAD();
        Commit curCommit = Commit.readFromFile(head);

        /*if (bufferAdd.contain(fileName)) {
            Utils.writeContents(fileDir, (Object) bufferAdd.getContents(fileName));
        } */
        if (curCommit != null && curCommit.fileIsStacked(fileName)) {
            byte[] contents = curCommit.getStackedFileContents(fileName);
            writeContents(fileDir, (Object) contents);
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    //辅助方法 ↓
    private static Commit getThatCommit(String commitID) {
        Commit targetCommit = null;
        int flag = 0;

        for (File file : COMMIT_DIR.listFiles()) {
            if (file.getName().startsWith(commitID)) {
                flag += 1;
                targetCommit = Commit.readFromFile(file.getName());
            }
        }

        if (1 == flag) {
            return targetCommit;
        } else {
            return null;
        }
    }

    public static void checkoutCommitFile(String commitID, String fileName) {
        // 依旧先载入这一块
        File fileDir = join(CWD, fileName);
        Branch branch = Branch.readFromFile();
        Commit thatCommit = getThatCommit(commitID);
        if (thatCommit == null) {
            System.out.println("No commit with that id exists.");
            return;
        } else {
            if (thatCommit.fileIsStacked(fileName)) {
                byte[] contents = thatCommit.getStackedFileContents(fileName);
                writeContents(fileDir, (Object) contents);
            } else {
                System.out.println("File does not exist in that commit.");
            }
        }
    }

    private static void update(Commit cur, Commit tar) {
        File file;
        // 删除当前分支的文件
        for (String curf : cur.getStackedBlob().keySet()) {
            if (!tar.containStacked(curf)) {
                file = join(CWD, curf);
                file.delete();
            }
        }

        // 创建目标分支的文件
        for (String tarf : tar.getStackedBlob().keySet()) {
            file = join(CWD, tarf);
            writeContents(file, tar.getStackedFileContents(tarf));
        }
    }

    public static void checkoutBranch(String branchName) {
        // 载入所有工作目录下的文件非文件夹
        File fileDir = join(CWD);

        // 依旧
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        Branch branch = Branch.readFromFile();
        if (branch.isCurrentBranch(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        } else if (!branch.branchIsContained(branchName)) {
            System.out.println("No such branch exists.");
            return;
        } else {
            String commitSha1 = branch.getHEAD();
            Commit curCommit = Commit.readFromFile(commitSha1);
            String targetHead = branch.getTargetHead(branchName);
            Commit targetCommit = Commit.readFromFile(targetHead);

            List<String> files = plainFilenamesIn(fileDir);
            String fileName;
            for (String fileStr : files) {
                boolean isStacked = curCommit.containStacked(fileStr);
                boolean isBuffer = bufferAdd.contain(fileStr);
                if (!isStacked && !isBuffer && targetCommit.containStacked(fileStr)) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    return;
                }
            }

            // 更新状态
            update(curCommit, targetCommit);

            // 切换分支
            branch.goToBranch(branchName);

            // 清除BufferAdd映射表
            bufferAdd.clear();
            bufferAdd.save();
            branch.save();

        }
    }

    public static void branch(String branchName) {
        // 取出branch读入内存
        Branch branch = Branch.readFromFile();
        if (!branch.createBranch(branchName)) {
            return;
        }
        // 记得保存
        branch.save();
    }

    public static void rmBranch(String branchName) {
        // 取出branch入内存
        Branch branch = Branch.readFromFile();
        if (!branch.branchIsContained(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (branch.isCurrentBranch(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        } else {
            branch.removeBranch(branchName);
        }

        branch.save();
    }

    public static void reset(String commitID) {
        Branch branch = Branch.readFromFile();
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        BufferRm bufferRm = BufferRm.readFromFile();
        /*UnStacked unStackedFiles = UnStacked.readFromFile();*/

        Commit curCommit = getThatCommit(branch.getHEAD());
        Commit targetCommit = getThatCommit(commitID);
        if (targetCommit == null) {
            System.out.println("No commit with that id exists.");
            return;
        }

        List<String> files = plainFilenamesIn(CWD);
        String fileName;
        for (String fileStr : files) {
            boolean isStacked = curCommit.containStacked(fileStr);
            boolean isBuffer = bufferAdd.contain(fileStr);
            if (!isStacked && !isBuffer && targetCommit.containStacked(fileStr)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }

        update(curCommit, targetCommit);
        targetCommit.writeFilesFromStacked();
        branch.setHEAD(commitID);
        /*// 从暂存区中获取文件到为追踪文件集合
        unStackedFiles.getFileFromBuffer(bufferAdd);*/
        branch.updateCurBranch(targetCommit.getSha1());
        bufferAdd.clear();
        bufferAdd.save();
        branch.save();
    }

    // 最近共同祖先的辅助方法，首次遍历当前分支的所有边
    private static HashSet<String> curBranchAllPoint(String curCommitID) {
        // 定义优先队列
        PriorityQueue<Commit> queue = new PriorityQueue<Commit>(new Comparator<Commit>() {
            @Override
            // 取最大堆
            public int compare(Commit o1, Commit o2) {
                return -(o1.getDepth() - o2.getDepth());
            }
        });

        // 定义并查集
        HashSet<String> set = new HashSet<>();

        // 取出对应commit
        Commit curCommit = Commit.readFromFile(curCommitID);
        Commit outCommit = null;
        Commit firstCommit = null;
        Commit secondCommit = null;
        queue.add(curCommit);
        while (!queue.isEmpty()) {
            outCommit = queue.poll();
            // 弹出元素并计入并查集
            set.add(outCommit.getSha1());
            if (!outCommit.getLast().equals(" ")) {
                firstCommit = Commit.readFromFile(outCommit.getLast());
            }
            // 如果当前提交包含上次的次提交
            if (!outCommit.getSecondLast().equals(" ")) {
                secondCommit = Commit.readFromFile(outCommit.getSecondLast());
            }
            // 过滤相同提交，优化时间
            if (firstCommit != null && set.contains(firstCommit.getSha1())) {
                continue;
            }
            if (firstCommit != null) {
                queue.add(firstCommit);
            }
            // 如果次提交不为null记录
            if (secondCommit != null) {
                queue.add(secondCommit);
            }
            secondCommit = null;
        }
        return set;
    }

    // 获取分裂点
    private static String getSplitBranch(String curBranchCommit, String tarBranchCommit) {
        // 定义优先队列
        PriorityQueue<Commit> queue = new PriorityQueue<Commit>(new Comparator<Commit>() {
            @Override
            public int compare(Commit o1, Commit o2) {
                return -(o1.getDepth() - o2.getDepth());
            }
        });
        String splitCommitID = "";
        HashSet<String> commitSet = curBranchAllPoint(curBranchCommit);

        Commit tarCommit = Commit.readFromFile(tarBranchCommit);
        Commit outCommit = null;
        Commit firstCommit = null;
        Commit secondCommit = null;
        queue.add(tarCommit);
        while (!queue.isEmpty()) {
            outCommit = queue.poll();
            // 弹出元素计入并查集
            if (commitSet.contains(outCommit.getSha1())) {
                splitCommitID = outCommit.getSha1();
                break;
            }
            if (!outCommit.getLast().equals(" ")) {
                firstCommit = Commit.readFromFile(outCommit.getLast());
            }
            // 如果当前提交的包含上次的此提交
            if (!outCommit.getSecondLast().equals(" ")) {
                secondCommit = Commit.readFromFile(outCommit.getSecondLast());
            }
            /*// 过滤相同提交，优化时间
            if (queue.contains(firstCommit)) {
                continue;
            }*/
            queue.add(firstCommit);
            // 如果次提交不为null记录
            if (secondCommit != null) {
                queue.add(secondCommit);
            }
            secondCommit = null;
        }
        return splitCommitID;

    }

    // 辅助获取全部文件集合
    private static HashSet<String> getAllFilesSet(Commit split, Commit cur, Commit tar) {
        HashSet<String> resultSet = new HashSet<String>();
        if (split.getStackedBlob() != null) {
            resultSet.addAll(split.getStackedBlob().keySet());
        }
        if (cur.getStackedBlob() != null) {
            resultSet.addAll(cur.getStackedBlob().keySet());
        }
        if (tar.getStackedBlob() != null) {
            resultSet.addAll(tar.getStackedBlob().keySet());
        }
        return resultSet;
    }

    // 辅助获取三个文件映射
    private static HashMap<String, String> getFilesMapFromOne(Commit oneCommit) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (oneCommit.getStackedBlob() != null) {
            resultMap.putAll(oneCommit.getStackedBlob());
        }
        return resultMap;
    }

    // 辅助检出文件
    private static void mergeCheckout(String fileName, byte[] contents) {
        File dir = Utils.join(CWD, fileName);
        Utils.writeContents(dir, contents);
        return;
    }

    // 删除文件
    private static void deleFile(String fileName) {
        File dir = Utils.join(CWD, fileName);
        // 受限制的删除
        Utils.restrictedDelete(dir);
        return;
    }

    // 合并文件
    private static void mergeFileContents(String fileName, byte[] curCont, byte[] tarCont) {
        // 文件路径
        File fileDir = Utils.join(CWD, fileName);
        String curStr;
        String tarStr;
        StringBuilder contentsOfFile = new StringBuilder("<<<<<<< HEAD\n");
        if (curCont == null) {
            curStr = "\n";
            tarStr = new String(tarCont, StandardCharsets.UTF_8);
        } else if (tarCont == null) {
            tarStr = "\n";
            curStr = new String(curCont, StandardCharsets.UTF_8);
        } else {
            curStr = new String(curCont, StandardCharsets.UTF_8);
            tarStr = new String(tarCont, StandardCharsets.UTF_8);
        }
        contentsOfFile.append(curStr);
        contentsOfFile.append("\n" + "=======");
        contentsOfFile.append(tarStr);
        contentsOfFile.append("\n" + ">>>>>>>");
        Utils.writeObject(fileDir, contentsOfFile);
    }

    // 统计未当前提交中的未追踪文件
    private static HashSet<String> getUnstacked(Commit tarCommit) {
        List<String> filesList = Utils.plainFilenamesIn(CWD);
        HashSet<String> resultSet = new HashSet<>();
        // 遍历文件列表
        for (String fileName : filesList) {
            // 不存在与stackedBlob中的文件被计入resultSet中
            if (!tarCommit.containStacked(fileName)) {
                resultSet.add(fileName);
            }
        }
        return resultSet;
    }

    private static boolean isUnstackedFail(Commit curCommit, HashMap<String, String> curFilesMap,
                                           HashMap<String, String> splitFilesMap,
                                           HashMap<String, String> tarFilesMap) {
        Blob splitB = null;
        Blob curB = null;
        Blob tarB = null;
        // 筛选当前分支的未跟踪文件
        HashSet<String> unstacked = getUnstacked(curCommit);
        for (String fileName : unstacked) {
            if (splitFilesMap.containsKey(fileName)) {
                splitB = Blob.readFromFile(splitFilesMap.get(fileName));
            } else {
                splitB = null;
            }
            if (curFilesMap.containsKey(fileName)) {
                curB = Blob.readFromFile(curFilesMap.get(fileName));
            } else {
                curB = null;
            }
            if (tarFilesMap.containsKey(fileName)) {
                tarB = Blob.readFromFile(tarFilesMap.get(fileName));
            } else {
                tarB = null;
            }
            // 情况4
            if (splitB == null && curB == null && tarB != null) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return true;
                // 情况8(2)
            } else if (splitB != null && curB == null && tarB != null
                    && tarB.getContents() != splitB.getContents()) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return true;
            }
        }
        return false;
    }

    private static Blob getBlob(String fileName,
                                HashMap<String, String> filesMap) {
        Blob blob1 = null;
        if (filesMap.containsKey(filesMap)) {
            blob1 = Blob.readFromFile(filesMap.get(fileName));
        } else {
            blob1 = null;
        }
        return blob1;
    }

    // 完成merge的工作区文件修改
    private static boolean modifyOfMerge(HashSet<String> allFileSet,
                                         HashMap<String, String> splitFilesMap,
                                         HashMap<String, String> curFilesMap,
                                         HashMap<String, String> tarFilesMap) {
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        BufferRm bufferRm = BufferRm.readFromFile();
        boolean isConflict = false;
        for (String fileName : allFileSet) {
            Blob splitB = getBlob(fileName, splitFilesMap);
            Blob curB = getBlob(fileName, curFilesMap);
            Blob tarB = getBlob(fileName, tarFilesMap);
            // split, cur, tar中均存在该文件
            if (splitB != null && curB != null && tarB != null) {
                // 1.cur中文件未修改，tar中文件修改 --> 文件从给定分支中检出并暂存
                if (splitB.getContents() == curB.getContents()
                        && splitB.getContents() != tarB.getContents()) {
                    mergeCheckout(fileName, tarB.getContents());
                    bufferAdd.add(fileName, tarB.getContents()); // 暂存暂存文件
                }
                // 2.cur中文件修改，tar中文件未修改 --> 保持原样
                if (splitB.getContents() != curB.getContents()
                        && splitB.getContents() == tarB.getContents()) {
                    continue;
                }
                // 3.cur, tar一同修改且内容一致 --> 原样不变
                if (curB.getContents() == tarB.getContents()
                        && curB.getContents() != splitB.getContents()) {
                    continue;
                }
                // 4.split, cur不存在该文件，tar存在该文件 --> 检出该文件并暂存
            } else if (splitB == null && curB == null && tarB != null) {
                // 检出文件
                mergeCheckout(fileName, tarB.getContents());
                // 暂存文件
                bufferAdd.add(fileName, tarB.getContents());
                // 5.split, tar不存在该文件，cur存在该文件 --> 原样保持
            } else if (splitB == null && curB != null && tarB == null) {
                continue;
                // 6.split, cur存在该文件，tar不存在该文件，且该文件在cur中未修改 --> 删除，并标记为未跟踪状态
            } else if (splitB != null && curB != null && tarB == null
                    && curB.getContents() == splitB.getContents()) {
                // 删除该文件
                deleFile(fileName);
                bufferRm.add(fileName);
                // 7.split, tar存在该文件，cur不存在该文件，且该文件在cur中未修改 --> 保持原样
            } else if (splitB != null && curB == null && tarB != null
                    && tarB.getContents() == splitB.getContents()) {
                continue;
                // 8.冲突状况, 全部递交给暂存区，8(1).split存在该文件，cur中该文件被修改，tar中该文件被删除
            } else if (splitB != null && curB != null && tarB == null
                    && curB.getContents() != splitB.getContents()) {
                mergeFileContents(fileName, curB.getContents(), null);
                bufferAdd.add(fileName, Utils.readContents(join(CWD, fileName)));
                isConflict = true;
                // 8(2).split存在该文件，cur中该文件被删除，tar中该文件被修改
            } else if (splitB != null && curB == null && tarB != null
                    && tarB.getContents() != splitB.getContents()) {
                mergeFileContents(fileName, null, tarB.getContents());
                bufferAdd.add(fileName, Utils.readContents(join(CWD, fileName)));
                isConflict = true;
                // 8(3).split存在该文件，cur, tar均修改该文件，且内容不一致
            } else if (splitB != null && curB != null && tarB != null
                    && tarB.getContents() != splitB.getContents()
                    && curB.getContents() != splitB.getContents()
                    && curB.getContents() != tarB.getContents()) {
                mergeFileContents(fileName, curB.getContents(), tarB.getContents());
                bufferAdd.add(fileName, Utils.readContents(join(CWD, fileName)));
                isConflict = true;
                // 8(4).split不存在该文件，cur, tar均添加该文件，且内容不一致
            } else if (splitB == null && curB != null & tarB != null
                    && tarB.getContents() != curB.getContents()) {
                mergeFileContents(fileName, curB.getContents(), tarB.getContents());
                bufferAdd.add(fileName, Utils.readContents(join(CWD, fileName)));
                isConflict = true;
            }
        }
        // 保存buffer，branch以便commit方法使用数据
        bufferAdd.save();
        bufferRm.save();
        return isConflict;
    }

    public static void merge(String tarBranch) {
        Branch branch = Branch.readFromFile();
        // 如果存在已暂存的添加或删除没有被提交：
        if (stagedUncommitFail()) {
            return;
        }
        // 如果给定的分支名称不存在, 输出错误信息:
        if (tarBranchNotExist(branch, tarBranch)) {
            return;
        }
        // 如果当前分支将于自身分支合并，输出错误信息
        if (mergeItselfFail(branch, tarBranch)) {
            return;
        }
        // 获取两个分支的最新提交，获取两分支的共同祖先
        String curBranchCommitID = branch.getHEAD();
        String tarBranchCommitID = branch.getTargetHead(tarBranch);
        String splitCommitID = getSplitBranch(curBranchCommitID, tarBranchCommitID);
        // 如果split为给定分支，不进行操作
        if (tarBranchCommitID.equals(splitCommitID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        Commit curCommit = Commit.readFromFile(curBranchCommitID);
        Commit tarCommit = Commit.readFromFile(tarBranchCommitID);
        Commit splitCommit = Commit.readFromFile(splitCommitID);
        // 如果split为当前分支，检出给定分支点，不改变当前分支
        if (curBranchCommitID.equals(splitCommitID)) {
            // 更新工作区, 参数为Commit
            update(curCommit, tarCommit);
            // 当前分支更新该提交
            branch.updateCurBranchHead(tarBranchCommitID);
            System.out.println("Current branch fast-forwarded.");
            branch.save();
            return;
        }
        // 获取四个映射 (allFiles, split, current, target)
        HashSet<String> allFileSet = getAllFilesSet(splitCommit, curCommit, tarCommit);
        HashMap<String, String> splitFilesMap = getFilesMapFromOne(splitCommit);
        HashMap<String, String> curFilesMap = getFilesMapFromOne(curCommit);
        HashMap<String, String> tarFilesMap = getFilesMapFromOne(tarCommit);
        // 检查当前提交中的未跟踪文件是否会被此次merge覆盖或者删除
        boolean unstackedFail = isUnstackedFail(curCommit, curFilesMap, splitFilesMap, tarFilesMap);
        if (unstackedFail) {
            return;
        }
        // 检查是否存在冲突
        if (modifyOfMerge(allFileSet, splitFilesMap, curFilesMap, tarFilesMap)) {
            System.out.println("Encountered a merge conflict.");
        }
        // 更新完毕，生成提交ing...
        String curBranch = branch.getCurrentBranch();
    commit("Merged " + tarBranch + " into " + curBranch + ".",
                curBranchCommitID, tarBranchCommitID);
    }

    // m处理erge时存在已暂存的添加或删除操作未提交的错误
    private static boolean stagedUncommitFail() {
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        BufferRm bufferRm = BufferRm.readFromFile();
        // 如果存在已暂存的添加或删除操作，输出错误信息：
        if (bufferAdd.isEmpty() || bufferRm.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return true;
        }
        return false;
    }

    // 处理merge时分支名称不存在的错误
    private static boolean tarBranchNotExist(Branch branch, String tarBranch) {
        // 如果当前分支将于自身分支合并，输出错误信息
        if (branch.branchIsContained(tarBranch)) {
            System.out.println("A branch with that name does not exist.");
            return true;
        }
        return false;
    }

    private static boolean mergeItselfFail(Branch branch, String tarBranch) {
        // 获取当前分支名称
        String curBranch = branch.getCurrentBranch();
        // 如果当前分支将于自身分支合并，输出错误信息
        if (curBranch.equals(tarBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return true;
        }
        return false;
    }
}

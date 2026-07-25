package gitlet;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;


import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    private static String userName = "user";
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    // 对象目录
    public static final File OBJECT_DIR = join(GITLET_DIR, "object");
    public static final File COMMIT_DIR = join(OBJECT_DIR, "commit");
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    // 缓存目录
    public static final File BUFFER_DIR = join(GITLET_DIR, "buffer");
    // 分支目录
    public static final File BRANCH_DIR = join(GITLET_DIR, "branch");
    /* TODO: fill in the rest of this class. */
    public static void init() {
        // 创建目录
        if (!GITLET_DIR.mkdir()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        if (OBJECT_DIR.mkdir()) {
            COMMIT_DIR.mkdir();
            BLOB_DIR.mkdir();
        }
        BUFFER_DIR.mkdir();
        BRANCH_DIR.mkdir();
        // 初始化用户名，待完善
            // 用户名定义移出方法
            // 下次调用gitlet，用户名不会保存，值为null
        // 初始化各部件
        // 初始提交
        // 获取当前时间戳new Date().getTime();
        Commit initCommit = new Commit("initial commit", userName, 0, " ", null);
        initCommit.save();
        // 初始化分支，将当前提交纳入初始化分支，初始分支为master
        // 分支类具有HEAD属性，存放initCommit hash， 此时 HEAD = initCommit.getSha1
        // 分支类也具有currentBranch属性，存放当前分支路线的名称，此时currentBranch = "master"
        // 构造时自动序列化进硬盘, 通过Utils.join(Repository.BRANCH_DIR, "branch")地址重新反序列化操作
        Branch branch = new Branch(initCommit.getSha1());
        // 初始化缓存区
        BufferAdd bufferadd = new BufferAdd();
        BufferRm bufferrm = new BufferRm();
        // 存盘
        branch.save();
        bufferadd.save();
        bufferrm.save();
    }

    public static void add(String fileName) {
        // 写入要添加的文件地址
        File addFile = Utils.join(CWD, fileName);

        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        // 反序列化缓冲区(Add)
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        // 写入缓冲区
        byte[] contents = Utils.readContents(addFile);
        bufferAdd.add(addFile.getName(), contents);
        // 再次序列化缓冲区
        bufferAdd.save();
    }

    // commit辅助方法

    private static boolean isBlankMessage(String message) {
        return message.isEmpty() || message.trim().isEmpty();
    }

    private static Map<String, String> updateStacked
                        (Commit current, BufferAdd bufferAdd, BufferRm bufferRm) {
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

    public static void commit(String message) {
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
        String Head = branch.getHEAD();
        // 当前commit -> Head 载入
        Commit currentCommit = Commit.readFromFile(Head);
        // 更新追踪列表
        Map<String, String> stacked = updateStacked(currentCommit, bufferAdd, bufferRm);
        // 新建commit实例
        Commit newCommit = new Commit(message, userName, new Date().getTime(), Head, stacked);
        Head = newCommit.getSha1();
        // 重新设置到当前分支当前指针上
        branch.setHEAD(Head);
        branch.updateCurBranch(Head);
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
        File fileName = Utils.join(CWD, filename);
        String fileNameString = fileName.getPath();
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
        if (bufferAdd.contain(fileNameString)) {
            bufferAdd.remove(fileNameString);
            flag += 1;
        }
        // 情况2
        // 分支文件载入, 分支文件只做查看操作无需更新状态
        Branch branch = Branch.readFromFile();
        String Head = branch.getHEAD();
        // 当前分支当前指向的commit载入
        Commit currentCommit = Commit.readFromFile(Head);
        if (currentCommit.containStacked(fileNameString)) {
            fileName.delete();
            bufferRm.add(fileNameString);
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
        String Head = branch.getHEAD();

        logHelper(Head);
    }
    // 全局log
    public static void globalLog(){
        List<String> commitList;
        commitList = Utils.plainFilenamesIn(COMMIT_DIR);
        if (commitList != null) {
            for (String dir : commitList){
                // 载入
                Commit commitPrintLog = Commit.readFromFile(dir);
                System.out.println(commitPrintLog.toString());
            }
        }
    }

    public static void find(String message){
        List<String> commitList;
        commitList = Utils.plainFilenamesIn(COMMIT_DIR);
        if (commitList != null) {
            for (String dir : commitList) {
                Commit commitPrintID = Commit.readFromFile(dir);
                if (commitPrintID.isEqualsMessage(message)) {
                    System.out.print(commitPrintID.getSha1());
                }
            }
        }
    }

    public static void status() {
        // 操作先载入
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        BufferRm bufferRm = BufferRm.readFromFile();
        Branch branch = Branch.readFromFile();
        // 打印状态
        branch.showCurrentBranch();
        bufferAdd.showContain();
        bufferRm.showContain();
    }

    public static void checkoutFile(String fileName) {
        // 载入到内存
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        File fileDir = Utils.join(CWD, fileName);
        Branch branch = Branch.readFromFile();
        String Head = branch.getHEAD();
        Commit curCommit = Commit.readFromFile(Head);

        /*if (bufferAdd.contain(fileName)) {
            Utils.writeContents(fileDir, (Object) bufferAdd.getContents(fileName));
        } */
        if (curCommit != null && curCommit.fileIsStacked(fileName)) {
            byte[] contents = curCommit.getStackedFileContents(fileName);
            Utils.writeContents(fileDir, (Object) contents);
        }  else {
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
        File fileDir = Utils.join(CWD, fileName);
        Branch branch = Branch.readFromFile();
        Commit thatCommit = getThatCommit(commitID);
        if (thatCommit == null) {
            System.out.println("No commit with that id exists.");
            return;
        } else {
            if (thatCommit.fileIsStacked(fileName)) {
                byte[] contents = thatCommit.getStackedFileContents(fileName);
                Utils.writeContents(fileDir, (Object) contents);
            } else {
                System.out.println("File does not exist in that commit.");
            }
        }
    }

    public static void checkoutBranch(String branchName) {
        // 载入所有工作目录下的文件非文件夹
        File fileDir = Utils.join(CWD);
        File[] fileList = fileDir.listFiles();
        // 依旧
        BufferAdd bufferAdd = BufferAdd.readFromFile();
        Branch branch = Branch.readFromFile();
        if (branch.isCurrentBranch(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        } else if (branch.curBranchIsContained(branchName)){
            System.out.println("No such branch exists.");
            return;
        } else {
            String commitSha1 = branch.getHEAD();
            Commit curCommit = Commit.readFromFile(commitSha1);
            String targetHead = branch.getTargetHead(branchName);
            Commit targetCommit = Commit.readFromFile(targetHead);
            // 更新状态
            curCommit.update();
            for (String fileName : curCommit.getUnstackedFile()) {
                if (targetCommit.fileIsStacked(fileName)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return;
                }
            }
            // 切换分支
            branch.goToBranch(branchName);
            // 删除当前目录的所有文件
            if (fileList != null) {
                for (File file : fileList) {
                    if (curCommit.fileIsStacked(file.getName())) {
                        file.delete();
                    }
                }
            }
            // 清除BufferAdd映射表
            bufferAdd.clear();
            bufferAdd.save();
            targetCommit.writeFilesFromStacked();
        }
    }

    public static void branch(String branchName) {
        // 取出branch读入内存
        Branch branch = Branch.readFromFile();
        if (! branch.createBranch(branchName)) {
            return;
        }
    }

    public static void rmBranch(String branchName) {
        // 取出branch入内存
        Branch branch = Branch.readFromFile();
        if (branch.curBranchIsContained(branchName)) {
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

        Commit targetCommit = getThatCommit(commitID);
        if (targetCommit == null) { System.out.println("No commit with that id exists."); return; }
        rmDir();
        targetCommit.writeFilesFromStacked();
        branch.updateCurBranch(targetCommit.getSha1());
        bufferAdd.clear();
        bufferAdd.save();
    }

    public static void merge(String branchName) {

    }
    // 清空工作目录
    private static void rmDir() {
        for (File file : CWD.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
}

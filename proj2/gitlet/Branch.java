package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Branch implements Serializable {

    // 构造时，传入第一个提交将此提交纳入初始分支master，并设置HEAD为当前commit
    public Branch(String sha1Ofcommit) {
        branchMap = new HashMap<>();
        branchMap.put("master", sha1Ofcommit);
        currentBranch = "master";
        HEAD = sha1Ofcommit;
    }

    public String goToOtherBranch(String branchKey) {
        return branchMap.get(branchKey);
    }

    // 提交时更新当前分支的提交追踪
    public void updateCurBranch(String curHead) {
        branchMap.put(currentBranch, curHead);
    }

    // 分支是否已创建
    public boolean curBranchIsContained(String branchName) {
        return branchMap.containsKey(branchName);
    }

    // 创建分支
    public boolean createBranch(String branchName) {
        if (curBranchIsContained(branchName)) {
            System.out.println("A branch with that name already exists.");
            return false;
        }
        branchMap.put(branchName, HEAD);
        return true;
    }

    // 保存文件
    public void save() {
        File BranchFile = Utils.join(Repository.BRANCH_DIR, "branch");
        Utils.writeObject(BranchFile, this);
    }

    // 获取当前分支头节点
    public String getHEAD() {
        return HEAD;
    }

    // 设置当前分支头节点
    public void setHEAD(String commitSha1) {
        HEAD = commitSha1;
    }

    // 是否当前分支
    public boolean isCurrentBranch(String branchName) {
        return currentBranch.equals(branchName);
    }

    // 显示当前节点状态
    public void showCurrentBranch() {
        System.out.print(this.toString());
    }

    // 更新分支节点
    public void updateCurBranchHead(String commitID) {
        branchMap.put(currentBranch, commitID);
        HEAD = branchMap.get(currentBranch);
    }

    // 格式化自身数据
    @Override
    public String toString() {
        StringBuilder branchMessage = new StringBuilder("=== Branches ===\n");
        List<String> keySet = new ArrayList<>(branchMap.keySet());
        Collections.sort(keySet);
        for (String name : keySet) {
            // 如果当前与currentBranch匹配标记该分支
            if (Objects.equals(name, currentBranch))
                branchMessage.append("*").append(name).append("\n");
            else
                branchMessage.append(name).append("\n");
        }

        return branchMessage.toString();
    }

    public String getTargetHead(String branchName) {
        return branchMap.get(branchName);
    }

    // 切换分支, 无需处理不存在分支, 交给
    public void goToBranch(String branchName) {
        // 更改当前分支名
        // 取出目标分支的当前提交节点
        // 更该当前HEAD指向commit节点
        currentBranch = branchName;
        HEAD = branchMap.get(currentBranch);
    }

    // 删除分支
    public void removeBranch(String branchName) {
        branchMap.remove(branchName);
    }

    // 读取文件
    public static Branch readFromFile() {
        File BranchFile = Utils.join(Repository.BRANCH_DIR, "branch");
        return Utils.readObject(BranchFile, Branch.class);
    }

    // branchMap<String -> branchName, String -> commitSha1>
    private Map<String, String> branchMap;
    // 当前分支（分支名称）
    private String currentBranch;
    // 当前分支的当前Commit（commit hash）
    private String HEAD;
}

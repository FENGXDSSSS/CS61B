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

    public void createBranch(String branchName) {
        branchMap.put(branchName, HEAD);
    }

    public void save() {
        File BranchFile = Utils.join(Repository.BRANCH_DIR, "branch");
        Utils.writeObject(BranchFile, this);
    }

    public String getHEAD() {
        return HEAD;
    }

    public void setHEAD(String commitSha1) {
        HEAD = commitSha1;
    }

    public void showCurrentBranch() {
        System.out.print(this.toString());
    }

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

    public static Branch readFromFile() {
        File BranchFile = Utils.join(Repository.BRANCH_DIR, "branch");
        return Utils.readObject(BranchFile, Branch.class);
    }

    // TODO 切换分支
    // branchMap<String -> branchName, String -> commitSha1>
    private Map<String, String> branchMap;
    // 当前分支（分支名称）
    private String currentBranch;
    // 当前分支的当前Commit（commit hash）
    private String HEAD;
}

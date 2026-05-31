package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Branch implements Serializable {
    // 构造时，传入第一个提交将此提交纳入初始分支master，并设置HEAD为当前commit
    public Branch(String commit) {
        branchMap = new HashMap<>();
        branchMap.put("master", commit);
        currentBranch = "master";
        HEAD = commit;
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

    // TODO 切换分支
    private Map<String, String> branchMap;
    private String currentBranch;
    private String HEAD;
}

/**
 * Definition for binary tree
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
public class Solution {
    public int _solve1(TreeNode r, int p) {
        
        if (r.left == null && r.right == null) {
            return p * 10 + r.val;
        }
        
        if (r.left == null && r.right != null) {
            return _solve1(r.right, p * 10 + r.val);
        }
        
        if (r.left != null && r.right == null) {
            return _solve1(r.left, p * 10 + r.val);
        }
        
        if (r.left != null && r.right != null) {
            return _solve1(r.left, p * 10 + r.val)
                + _solve1(r.right, p * 10 + r.val);
        }
            
        // cannot reach here
        return 0;
    }
    
    public int sumNumbers(TreeNode root) {
        // corner case is interesting and dangerous
        // so it should be a habit to check it after
        // thinking the normal cases
        if (root == null) {
            return 0;
        }
        return _solve1(root, 0);
    }
}
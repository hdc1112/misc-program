public class Solution {
    
    int _count(Set<Integer> set, int v, boolean up) {
        int c = 0;
        
        while (set.contains(v)) {
            set.remove(v);
            c++;
            v = up ? v + 1 : v - 1;
        }
        
        return c;
    }
    
    int downwards(Set<Integer> set, int v) {
        return _count(set, v, false);    
    }
    
    int upwards(Set<Integer> set, int v) {
        return _count(set, v, true);
    }
    
    public int longestConsecutive(int[] num) {
        // Start typing your Java solution below
        // DO NOT write main() function
        Set<Integer> set = new HashSet<Integer>();
        for (int a : num) {
            set.add(a);
        }
        
        int ret = 0;
        for (int a : num) {
            if (set.contains(a)) {
                int new_ret = downwards(set, a)
                            + upwards(set, a + 1);
                if (new_ret > ret) {
                    ret = new_ret;
                }
            }
        }
        
        return ret;
    }
}
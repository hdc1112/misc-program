class Solution {
private:
    // input: a[s,e] inclusive
    // output: 
    // ret = 0 -> not a palindrome
    // otherwise -> is a palindrome
    int isPalindrome(char *a, int s, int e) {
        if (s > e) {
            return 0;
        }
        while (s <= e) {
            if (a[s] != a[e]) {
                return 0;
            }
            s++;
            e--;
        }
        return 1;
    }
    
    // input: len is length of array a
    // output: min-cut 
    int solve(char *a, int len) {
        int *arr_min = new int[len];
        const int rlimit = len - 1;

        for (int loop = rlimit; loop >= 0; loop--) {
            if (loop == rlimit) {
                arr_min[loop] = 0;
                continue;
            }
            
            int mini = len - 1; // like infinity
            for (int j = loop; j <= rlimit; j++) {
                if (j == rlimit) {
                    if (isPalindrome(a, loop, j)) {
                        mini = 0;
                        break;
                    }
                } else {
                    if (isPalindrome(a, loop, j)) {
                        if (1 + arr_min[j + 1] < mini) {
                            mini = 1 + arr_min[j + 1];
                        }
                    }
                }
            }
            arr_min[loop] = mini;
        }
        
        int ret = arr_min[0];
        delete[] arr_min;
        return ret;
    }
    
public:
    int minCut(string s) {
        // Start typing your C/C++ solution below
        // DO NOT write int main() function
        int len = s.length();   // doesn't count the '\0'
        char *cstr = new char[len + 1];
        strcpy(cstr, s.c_str());
        int ret = solve(cstr, len);
        delete[] cstr;
        return ret;
    }
};
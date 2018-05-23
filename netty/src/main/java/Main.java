import java.util.*;

/**
 * 合唱团
 *
 * 有 n 个学生站成一排，每个学生有一个能力值，
 * 牛牛想从这 n 个学生中按照顺序选取 k 名学生，
 * 要求相邻两个学生的位置编号的差不超过 d，
 * 使得这 k 个学生的能力值的乘积最大，
 * 你能返回最大的乘积吗
 *
 * 动态规划算法是通过拆分问题，定义问题状态和状态之间的关系，
 * 使得问题能够以递推（或者说分治）的方式去解决。[1]
 * 动态规划算法的基本思想与分治法类似，
 * 也是将待求解的问题分解为若干个子问题（阶段），按顺序求解子阶段，
 * 前一子问题的解，为后一子问题的求解提供了有用的信息。在求解任一子问题时，
 * 列出各种可能的局部解，通过决策保留那些有可能达到最优的局部解，丢弃其他局部解。
 * 依次解决各子问题，最后一个子问题就是初始问题的解.
 */
public class Main {

    private static final int MAX_NUM = 50;
    private static int[][] locations;
    private static int[][] numsTree;
    private static int[] result;
    private static int d;
    private static int k;
    private static int length;
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        Scanner scanner = new Scanner(System.in);
        int count = scanner.nextInt();
        length = count - 1;
        int[] nums = new int[count];
        for (int i = 0; i < count; i++) {
            nums[i] = scanner.nextInt();
        }
        d = scanner.nextInt();
        k = scanner.nextInt();

            int n = (int) Math.pow(d, k - 1);
            int[] maxResult = new int[count - k + 1];
            for (int m = 0; m <= count - k; m++) {
                result = new int[n];
                locations = new int[MAX_NUM][MAX_NUM];
                numsTree = new int[MAX_NUM][MAX_NUM];
                int num;
                locations[0][0] = m;
                //计算出符合要求的位置
                /**
                 * 此处得到位置的树
                 *          0
                 *        1  2
                 *      2 3  3 4
                 */
                for (int i = 1; i < k; i++) {
                    num = (int) Math.pow(d, i);
                    for (int j = 0; j < num; j++) {
                        getLocation(i, j);
                    }
                }
                /**
                 * 此处得到数据的树
                 *          0
                 *        1  2
                 *      2 3  3 4
                 */
                //根据位置取出数据
                for (int i = 0; i < k; i++) {
                    num = (int) Math.pow(d, i);
                    for (int j = 0; j < num; j++) {
                        if (locations[i][j] == -1) {
                            numsTree[i][j] = -51;
                        } else {
                            numsTree[i][j] = nums[locations[i][j]];
                        }
                    }
                }
                //计算结果，通过递归计算结果
                for (int i = 0; i < n; i++) {
                    result[i] = main.getResult(k - 1, i);
                }
                Arrays.sort(result);
                maxResult[m] = result[n - 1];
            }
            Arrays.sort(maxResult);
            System.out.println(maxResult[count - k]);
    }


    public int getResult(int r, int j) {
        if (r == 0) {
            return  numsTree[0][0];
        }
        if (numsTree[r][j] == -51) {
            return 1 * getResult(r-1,j/d);
        }
        return numsTree[r][j] * getResult(r-1,j/d);
    }

    public static void getLocation(int r,int j) {
        if (r == 0) {
           locations[r][j] =  locations[0][0];
        }
        if (locations[r-1][j/d] == -1){
            locations[r][j] = -1;
            return;
        }
        locations[r][j] =  locations[r-1][j/d] + (j%d) + 1;
        if (locations[r][j] > length) {
            locations[r][j] = -1;
        }
    }

}

package SegmentTree_BinaryIndexTree;

/**
 * Given a 2D matrix matrix, find the sum of the elements inside the rectangle
 * defined by its upper left corner (row1, col1) and lower right corner (row2,
 * col2).
 * Example:
Given matrix = [
  [3, 0, 1, 4, 2],
  [5, 6, 3, 2, 1],
  [1, 2, 0, 1, 5],
  [4, 1, 0, 1, 7],
  [1, 0, 3, 0, 5]
]

sumRegion(2, 1, 4, 3) -> 8
update(3, 2, 2)
sumRegion(2, 1, 4, 3) -> 10
Note:
The matrix is only modifiable by the update function.
You may assume the number of calls to update and sumRegion function is distributed evenly.
You may assume that row1 ≤ row2 and col1 ≤ col2.
 * @author kaizhang
 *
 */
public class Range_Sum_Query_2D_Mutable {
	private int[][] source;
	private int[][] tree;
    public Range_Sum_Query_2D_Mutable(int[][] matrix) {
        if(matrix == null || matrix.length == 0 || matrix[0].length == 0){
            return;
        }
        this.source = matrix;
        this.tree = new int[matrix.length + 1][matrix[0].length + 1];
        for(int i = 1; i < tree.length; i++) {
            for(int j = 1; j < tree[0].length; j++) {
                updateTree(i, j, matrix[i - 1][j  - 1] );
            }
        }
    }
    public void updateTree(int x, int y, int diff) {
        while(x < tree.length) {
            int y1 = y;
            while(y1 < tree[0].length) {
                tree[x][y1] += diff;
                y1 = getNext(y1);
            }
            x = getNext(x);
        }
    }
    public int getSum(int x, int y) {
        int sum = 0;
        while(x > 0) {
            int y1 = y;
            while(y1  > 0){
             sum += tree[x][y1];
             y1 = getParent(y1);
            }
            x = getParent(x);
           
        }
        return sum;
    }
    private int lowBit(int x) {
        return x & -x;
    }
    private int getNext(int x) {
        return x + lowBit(x);
    }
    private int getParent(int x) {
        return x - lowBit(x);
    }
    

    public void update(int row, int col, int val) {
        updateTree(row + 1, col + 1,val - source[row ][col]);
    }

    public int sumRegion(int row1, int col1, int row2, int col2) {
        if (tree == null || row1 < 0 || row2 >= tree.length - 1  || col1 < 0 || col2 >= tree[0].length - 1) {
            return 0;
        }
       
       return getSum(row2 + 1, col2 + 1) - getSum(row1, col2 + 1) - getSum(row2 + 1, col1) + getSum(row1, col1);
    }
    public static void main(String[] args) {
    	int[][] arr = new int[][] {{3,0,1,4,2},{5,6,3,2,1},{1,2,0,1,5},{4,1,0,1,7},{1,0,3,0,5}};
    	int [][] arr1 = new int[][] {{2,4}, {-3,5}};
    	Range_Sum_Query_2D_Mutable test = new Range_Sum_Query_2D_Mutable(arr1);
//    	System.out.println(test.sumRegion(2, 1, 4, 3));
//    	test.update(3, 2, 2);
//    	System.out.println(test.sumRegion(2, 1, 4, 3));
    	test.update(0,1,3);
    	test.update(1,1,-3);
    	test.update(0,1,1);
    	System.out.println(test.sumRegion(0,0,1,1));
    }
}

package SegmentTree_BinaryIndexTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * You are given an integer array nums and you have to return a new counts
 * array. The counts array has the property where counts[i] is the number of
 * smaller elements to the right of nums[i].
 * 
 * Example:
 * 
 * Given nums = [5, 2, 6, 1]
 * 
 * To the right of 5 there are 2 smaller elements (2 and 1). To the right of 2
 * there is only 1 smaller element (1). To the right of 6 there is 1 smaller
 * element (1). To the right of 1 there is 0 smaller element. Return the array
 * [2, 1, 1, 0].
 * 
 * 
 * 
 * @author kaizhang
 *
 */
public class Count_Of_Smaller_Numbers_After_Itself {
	// version 1: construct BIT
	public List<Integer> countSmaller(int[] nums) {
		List<Integer> list = new LinkedList<Integer>();
		int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
		for (int i = 0; i < nums.length; i++) {
			max = Math.max(max, nums[i]);
			min = Math.min(min, nums[i]);
		}
		int[] bit = new int[max - min + 1 + 1];
		for (int i = nums.length - 1; i >= 0; i--) {
			int index = nums[i] - min + 1;
			update(bit, index, 1);
			list.add(0, getSum(bit, index - 1));
		}
		return list;
	}

	private int lowbit(int x) {
		return x & -x;
	}

	private int getNext(int x) {
		return x + lowbit(x);
	}

	private int getParent(int x) {
		return x - lowbit(x);
	}

	private int getSum(int[] arr, int index) {
		int sum = 0;
		while (index > 0) {
			sum += arr[index];
			index = getParent(index);
		}
		return sum;
	}

	private void update(int[] arr, int index, int diff) {
		while (index < arr.length) {
			arr[index] += diff;
			index = getNext(index);
		}
		return;
	}

	// version 2: Construct segment Tree
	// class SegmentTree {
	// int start;
	// int end;
	// int count;
	// SegmentTree left, right;
	//
	// public SegmentTree(int a, int b) {
	// if (a > b) {
	// return;
	// }
	// this.count = 0;
	// this.start = a;
	// this.end = b;
	// if (a != b) {
	// int mid = (a + b) / 2;
	// this.left = new SegmentTree(a, mid);
	// this.right = new SegmentTree(mid + 1, b);
	// }
	// }
	//
	// private void update(int l, int r) {
	// if (start == l && r == end) {
	// ++count;
	// return;
	// }
	// if (l > end || r < start) {
	// return;
	// }
	//
	// int mid = (start + end) / 2;
	// if (r <= mid) {
	// left.update(l, r);
	// } else if (l >= mid + 1) {
	// right.update(l, r);
	// } else {
	// left.update(l, mid);
	// right.update(mid + 1, r);
	// }
	// count = left.count + right.count;
	//
	// }
	//
	// private int query(int l, int r) {
	// if (l == start && r == end) {
	// return this.count;
	// }
	// if (l > end || r < start) {
	// return 0;
	// }
	// int mid = (start + end) / 2;
	// if (r <= mid) {
	// return this.left.query(l, r);
	// } else if (l >= mid + 1) {
	// return this.right.query(l, r);
	// } else {
	//
	// return this.left.query(l, mid) + this.right.query(mid + 1, r);
	// }
	// }
	// }
	//
	// public List<Integer> countSmaller_SegTree(int[] nums) {
	// List<Integer> list = new LinkedList<Integer>();
	// int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
	// for (int i = 0; i < nums.length; i++) {
	// max = Math.max(max, nums[i]);
	// min = Math.min(min, nums[i]);
	// }
	// SegmentTree root = new SegmentTree(min, max);
	// for (int i = nums.length - 1; i >= 0; i--) {
	// list.add(0, root.query(min, nums[i] - 1));
	// root.update(nums[i], nums[i]);
	// }
	// return list;
	//
	// }
	class segTree {
		segTree left, right;
		int start, end, count;

		segTree(int start, int end) {
			this.start = start;
			this.end = end;
			this.count = 0;
			this.left = this.right = null;
		}
	}

	private segTree root;

	private segTree buildTree(int start, int end) {
		if (start > end) {
			return null;
		}
		segTree node = new segTree(start, end);
		if (start != end) {
			int mid = start + (end - start) / 2;
			node.left = buildTree(start, mid);
			node.right = buildTree(mid + 1, end);
		}
		return node;
	}

	private void modify(segTree root, int start, int end) {
		if (start == root.start && end == root.end) {
			root.count += 1;
			return;
		}
		if (start > root.end || end < root.start) {
			return;
		}
		 int mid = start + (end - start) / 2;
		if (end <= mid) {
			modify(root.left, start, end);
		} else if (start > mid) {
			modify(root.right, start, end);
		} else {
			modify(root.left, start, mid);
			modify(root.right, mid + 1, end);
		}
		root.count = root.left.count + root.right.count;
		return;
	}

	private int search(segTree root, int start, int end) {
		if (start == root.start && end == root.end) {
			return root.count;
		}
		if (start > root.end || end < root.start) {
			return 0;
		}
		 int mid = start + (end - start) / 2;
		if (end <= mid) {
			return search(root.left, start, end);
		} else if (start > mid) {
			return search(root.right, start, end);
		} else {
			return search(root.left, start, mid)
					+ search(root.right, mid + 1, end);
		}
	}

	public List<Integer> countSmaller_SegTree(int[] nums) {
		List<Integer> list = new LinkedList<Integer>();
		int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
		for (int i = 0; i < nums.length; i++) {
			max = Math.max(max, nums[i]);
			min = Math.min(min, nums[i]);
		}
		root = buildTree(min, max);
		for (int i = nums.length - 1; i >= 0; i--) {
			list.add(0, search(root, min, nums[i] - 1));
			modify(root, nums[i], nums[i]);
		}
		return list;

	}

	public static void main(String[] args) {
		int[] arr = new int[] { 5, 2, 6, 1 };
		int[] arr1 = new int[] {  1723, 1802,
				1493, 9194, 5591, 3196, -223, 2691, -117};
				
		Count_Of_Smaller_Numbers_After_Itself test = new Count_Of_Smaller_Numbers_After_Itself();
		System.out.println(test.countSmaller(arr).toString());
		System.out.println(test.countSmaller_SegTree(arr).toString());

	}
}

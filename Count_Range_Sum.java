package Google;

import java.util.Arrays;

/**
 * Given an integer array nums, return the number of range sums that lie in
 * [lower, upper] inclusive. Range sum S(i, j) is defined as the sum of the
 * elements in nums between indices i and j (i ≤ j), inclusive.
 * 
 * Note: A naive algorithm of O(n2) is trivial. You MUST do better than that.
 * 
 * Example: Given nums = [-2, 5, -1], lower = -2, upper = 2, Return 3. The three
 * ranges are : [0, 0], [2, 2], [0, 2] and their respective sums are: -2, -1, 2.
 * 
 * Credits: Special thanks to @dietpepsi for adding this problem and creating
 * all test cases.
 * 
 * @author kaizhang
 *
 */
/**
 * tips : to avoid the  integer overflow. we can use long[] array
 * to store the prefix sum
 * 
 * @author kaizhang
 *
 */
public class Count_Range_Sum {
	// version 1: divide and conquer :
	public int countRangeSum_Div_Conquer(int[] nums, int lower, int upper) {
		long[] sums = new long[nums.length + 1];
		for (int i = 1; i <= nums.length; i++) {
			sums[i] = sums[i - 1] + nums[i - 1];
		}
		return merge_count(sums, 0, sums.length - 1, lower, upper);
	}

	private int merge_count(long[] sums, int left, int right, int lower,
			int upper) {
		if (left + 1 > right) {
			return 0;
		}
		int mid = (left + right) / 2;
		int count = merge_count(sums, left, mid, lower, upper)
				+ merge_count(sums, mid + 1, right, lower, upper);
		long[] cache = new long[right - left + 1];
		// for each element in left part of array , find the element in right
		// array satisfies that :
		// sums[j] - sums[i] < lower
		// sums[j] - sums[i] < upper
		int l = mid + 1, h = mid + 1, t = mid + 1, index = 0;
		for (int i = left; i <= mid; i++) {
			while (t <= right && sums[t] < sums[i]) {
				cache[index++] = sums[t];
				t++;
			}
			while (l <= right && sums[l] - sums[i] < lower) {
				l++;
			}
			while (h <= right && sums[h] - sums[i] <= upper) {
				h++;
			}
			count += h - l;
			cache[index++] = sums[i];
		}
		System.arraycopy(cache, 0, sums, left, index);
		return count;
	}

	// version 2 : use a bit to get count
	class BinaryIndexTree {
		private int[] arr;
		private int[] tree;

		public BinaryIndexTree(int[] arr) {
			this.arr = arr;
			buildTree();
		}

		private void buildTree() {
			this.tree = new int[arr.length + 1];
		}

		private void updateTree(int index, int diff) {
			// int diff = value - tree[index] ;
			while (index < tree.length) {
				tree[index] += diff;
				index = getNext(index);
			}
		}

		private int lowbit(int x) {
			return x & -x;
		}

		private int getNext(int index) {
			return index + lowbit(index);
		}

		private int getParent(int index) {
			return index - lowbit(index);
		}

		private int getSum(int index) {
			index++;
			int sum = 0;
			while (index > 0) {
				sum += tree[index];
				index = getParent(index);
			}
			return sum;
		}

	}

	public int countRangeSum_Bit(int[] nums, int lower, int upper) {
		if (nums == null || nums.length == 0) {
			return 0;
		}
		int[] sums = new int[nums.length + 1];
		int[] cands = new int[3 * sums.length];
		int index = 0;
		cands[index++] = 0;
		cands[index++] = upper;
		cands[index++] = lower - 1;
		for (int i = 1; i < sums.length; i++) {
			sums[i] = sums[i - 1] + nums[i - 1];
			cands[index++] = sums[i];
			cands[index++] = sums[i] + upper;
			cands[index++] = sums[i] + lower - 1;
		}
		Arrays.sort(cands);
		BinaryIndexTree bit = new BinaryIndexTree(cands);
		for (int i = 0; i < sums.length; i++) {
			int temp = Arrays.binarySearch(cands, sums[i]);
			bit.updateTree(temp + 1, 1);
		}

		int count = 0;
		for (int i = 1; i < sums.length; i++) {
			bit.updateTree(Arrays.binarySearch(cands, sums[i - 1]) + 1, -1);
			count += bit
					.getSum(Arrays.binarySearch(cands, sums[i - 1] + upper));
			count -= bit
					.getSum(Arrays.binarySearch(cands, sums[i - 1] + lower));

		}
		return count;

	}

	// answer for bit:
	public int countRangeSum(int[] nums, int lower, int upper) {
		long[] sum = new long[nums.length + 1];
		long[] cand = new long[3 * sum.length + 1];
		int index = 0;
		cand[index++] = sum[0];
		cand[index++] = lower + sum[0] - 1;
		cand[index++] = upper + sum[0];

		for (int i = 1; i < sum.length; i++) {
			sum[i] = sum[i - 1] + nums[i - 1];
			cand[index++] = sum[i];
			cand[index++] = lower + sum[i] - 1;
			cand[index++] = upper + sum[i];
		}

		cand[index] = Long.MIN_VALUE; // avoid getting root of the binary
										// indexed tree when doing binary search
		Arrays.sort(cand);

		int[] bit = new int[cand.length];

		// build up the binary indexed tree with only elements from the prefix
		// array "sum"
		for (int i = 0; i < sum.length; i++) {
			addValue(bit, Arrays.binarySearch(cand, sum[i]), 1);
		}

		int count = 0;

		for (int i = 1; i < sum.length; i++) {
			// get rid of visited elements by adding -1 to the corresponding
			// tree nodes
			addValue(bit, Arrays.binarySearch(cand, sum[i - 1]), -1);

			// add the total number of valid elements with upper bound (upper +
			// sum[i - 1])
			count += query(bit, Arrays.binarySearch(cand, upper + sum[i - 1]));

			// minus the total number of valid elements with lower bound (lower
			// + sum[i - 1] - 1)
			count -= query(bit,
					Arrays.binarySearch(cand, lower + sum[i - 1] - 1));
		}

		return count;
	}

	private void addValue(int[] bit, int index, int value) {
		while (index < bit.length) {
			bit[index] += value;
			index += index & -index;
		}
	}

	private int query(int[] bit, int index) {
		int sum = 0;

		while (index > 0) {
			sum += bit[index];
			index -= index & -index;
		}

		return sum;
	}

	public static void main(String[] args) {

		Count_Range_Sum test = new Count_Range_Sum();
		int arr[] = new int[] { -2, 5, -1 };
		int arr1[] = new int[] { -2147483647, 0, -2147483647, 2147483647 };
		int lower = -2;
		int upper = 2;
		// test for divide and conquer
		System.out.println(test.countRangeSum_Div_Conquer(arr, lower, upper));
		// test for bit :
		int[] sortarr = new int[] { 2, 3, 4, 5 };
		System.out.println(Arrays.binarySearch(sortarr, 2));
		System.out.println(test.countRangeSum_Bit(arr1, -345, 5678));
		System.out.println(test.countRangeSum(arr1, 0, 0));
		System.out.println(Integer.MIN_VALUE);
	}

}

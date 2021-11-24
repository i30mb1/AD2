package n7.ad2.parseinfo.algorithm

import java.util.ArrayDeque
import java.util.Stack
import kotlin.math.max

class TreeNode(var value: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

fun main() {
    val root = TreeNode(1).apply {
        left = TreeNode(4)
        right = TreeNode(2).apply {
            left = TreeNode(3)
            right = null
        }
    }

    preOrderTraversal2(root)
    preOrderTraversal(root)
    inOrderTraversal(root)
    postOrderTraversal(root)
    levelOrderTraversal(root)
}

fun invertTree(root: TreeNode?): TreeNode? {
    if (root == null) return null
    val left = invertTree(root.left)
    val right = invertTree(root.right)
    root.left = right
    root.right = left
    return root
}

var rootIndex = 0
fun constructBinaryTreeFromInOrderAndPostOrder(inOrder: IntArray, postOrder: IntArray): TreeNode? {
    rootIndex = postOrder.size - 1
    fun build(inOrder: IntArray, postOrder: IntArray, from: Int, to: Int): TreeNode? {
        if (from > to) return null
        val rootValue = postOrder[rootIndex--]
        val root = TreeNode(rootValue)
        if (from == to) return root
        val i = inOrder.indexOf(rootValue)
        root.left = build(inOrder, postOrder, from, i - 1)
        root.right = build(inOrder, postOrder, i + 1, to)
        return root
    }
    return build(inOrder, postOrder, 0, postOrder.size - 1)
}

fun hasPathSum(root: TreeNode?, sum: Int): Boolean {
    if (root == null) return false
    if (root.left == null && root.right == null && sum - root.value == 0) return true
    return hasPathSum(root.left, sum - root.value) || hasPathSum(root.right, sum - root.value)
}

fun isSymmetric(root: TreeNode?): Boolean {
    if (root == null) return true
    return isSymmetric(root.left, root.right)

}

fun isSymmetric(left: TreeNode?, right: TreeNode?): Boolean {
    if (left == null && right == null) return true
    if (left == null || right == null) return false
    if (left.value != right.value) return false
    return isSymmetric(left.left, right.right) && isSymmetric(left.right, right.left)
}

fun maxDepth(root: TreeNode?): Int {
    if (root == null) return 0
    val maxLeft = maxDepth(root.left)
    val maxRight = maxDepth(root.right)
    return max(maxLeft, maxRight) + 1
}

fun levelOrderTraversal(root: TreeNode): List<List<Int>> {
    val result = mutableListOf<MutableList<Int>>()
    val queue = ArrayDeque<TreeNode>()
    queue.addFirst(root)
    while (queue.isNotEmpty()) {
        var size = queue.size
        val list = ArrayList<Int>()
        while (size > 0) {
            val node = queue.pollLast()
            list.add(node.value)
            node.left?.let { queue.addFirst(it) }
            node.right?.let { queue.addFirst(it) }
            size--
        }
        result.add(list)
    }
    println("level order traversal $result")
    return result
}

fun preOrderTraversal2(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    fun recursive(node: TreeNode?) {
        if (node != null) {
            result.add(node.value)
            recursive(node.left)
            recursive(node.right)
        }
    }
    recursive(root)
    println("recursive preOrder traversal $result")
    return result
}

// https://youtu.be/5dySuyZf9Qg
// - visit the root, then left subtree, finally right subtree
fun preOrderTraversal(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    val stack = Stack<TreeNode>()
    stack.push(root)
    while (stack.isNotEmpty()) {
        val node = stack.pop()
        if (node != null) {
            result.add(node.value)
            stack.push(node.right)
            stack.push(node.left)
        }
    }
    println("iteratively preOrder traversal $result")
    return result
}

// - visit the left subtree, then root, finally right subtree
fun inOrderTraversal(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    val stack = Stack<TreeNode>()
    var curr = root
    while (stack.isNotEmpty() || curr != null) {
        while (curr != null) {
            stack.push(curr)
            curr = curr.left
        }
        curr = stack.pop()
        result.add(curr.value)
        curr = curr.right
    }
    println("recursive InOrder traversal $result")
    return result
}

// - visit the left subtree, then right subtree, finally root
fun postOrderTraversal(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    val stack = Stack<TreeNode>()
    stack.push(root)
    while (stack.isNotEmpty()) {
        val node = stack.pop()
        if (node != null) {
            result.add(0, node.value)
            stack.push(node.right)
            stack.push(node.left)
        }
    }
    println("recursive postOrder traversal $result")
    return result
}
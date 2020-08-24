package ad2.n7.parseinfo.algorithm

import java.util.*

class TreeNode(var value: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

fun main() {
    val root = TreeNode(1).apply {
        left = null
        right = TreeNode(2).apply {
            left = TreeNode(3)
            right = null
        }
    }

    preOrderTraversal(root)
    inOrderTraversal(root)
    postOrderTraversal(root)

}

fun preOrderTraversal(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    val stack = Stack<TreeNode>()
    stack.push(root)
    while (stack.isNotEmpty()) {
        val node = stack.pop()
        if (node != null) {
            result.add(node.value)
            stack.push(node.left)
            stack.push(node.right)
        }
    }
    println(result)
    return result
}

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
        result.add(curr?.value ?: 0)
        curr = curr?.right
    }
    println(result)
    return result
}

fun postOrderTraversal(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    val stack = Stack<TreeNode>()
    stack.push(root)
    while (stack.isNotEmpty()) {
        val node = stack.pop()
        if (node != null) {
            result.add(0, node.value)
            stack.push(node.left)
            stack.push(node.right)
        }
    }
    println(result)
    return result
}
package ad2.n7.parseinfo.algorithm

import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList

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
    levelOrderTraversal(root)
}

fun levelOrderTraversal(root: TreeNode): List<List<Int>> {
    val result = mutableListOf<MutableList<Int>>()
    val queue = java.util.ArrayDeque<TreeNode>()
    queue.add(root)
    while (queue.isNotEmpty()) {
        var size = queue.size
        val list = ArrayList<Int>()
        while(size > 0) {
            val node = queue.poll()
            list.add(node.value)
            node.left?.let { queue.add(it) }
            node.right?.let { queue.add(it) }
            size --
        }
        result.add(list)
    }
    println(result)
    return result
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
        result.add(curr.value)
        curr = curr.right
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
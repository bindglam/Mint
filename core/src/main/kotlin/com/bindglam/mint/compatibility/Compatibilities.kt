package com.bindglam.mint.compatibility

import com.bindglam.mint.utils.logger

fun printCompatIssue() {
    logger().warn("------------------------------------------------------------")
    logger().warn("  External database detected. Integration with plugins")
    logger().warn("  like Vault and PlaceholderAPI may cause thread")
    logger().warn("  blocking and degrade server performance. To optimize")
    logger().warn("  stability, consider using direct communication with")
    logger().warn("  the Mint plugin.")
    logger().warn("------------------------------------------------------------")
}
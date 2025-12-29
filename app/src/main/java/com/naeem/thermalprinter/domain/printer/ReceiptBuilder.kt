package com.naeem.thermalprinter.domain.printer

import com.naeem.thermalprinter.domain.model.Receipt

object ReceiptBuilder {
    private val commandBuffer = mutableListOf<ByteArray>()

    fun format(receipt: Receipt): ByteArray {
        commandBuffer.clear()

        // Initialize printer
        addCommand(ESCPOSCommands.INIT)
        addCommand(ESCPOSCommands.SIZE_NORMAL)

        // Business name - centered, bold, large
        addCommand(ESCPOSCommands.ALIGN_CENTER)
        addCommand(ESCPOSCommands.BOLD_ON)
        addCommand(ESCPOSCommands.SIZE_DOUBLE)
        addText("NAEEM DOCUMENTATION")
        addCommand(ESCPOSCommands.LF)
        addCommand(ESCPOSCommands.BOLD_OFF)
        addCommand(ESCPOSCommands.SIZE_NORMAL)

        // Blank line
        addCommand(ESCPOSCommands.LF)

        // Divider line
        addCommand(ESCPOSCommands.ALIGN_CENTER)
        addText("================================")
        addCommand(ESCPOSCommands.LF)

        // Blank line
        addCommand(ESCPOSCommands.LF)

        // Date
        addCommand(ESCPOSCommands.ALIGN_LEFT)
        addText("Date: ${receipt.formattedTimestamp()}")
        addCommand(ESCPOSCommands.LF)

        // Blank line
        addCommand(ESCPOSCommands.LF)

        // Divider
        addCommand(ESCPOSCommands.ALIGN_CENTER)
        addText("--------------------------------")
        addCommand(ESCPOSCommands.LF)

        // Amount - right aligned with currency
        addCommand(ESCPOSCommands.ALIGN_RIGHT)
        addCommand(ESCPOSCommands.BOLD_ON)
        addText("Amount:              ${receipt.formattedAmount()} ${receipt.currency}")
        addCommand(ESCPOSCommands.BOLD_OFF)
        addCommand(ESCPOSCommands.LF)

        // Divider
        addCommand(ESCPOSCommands.ALIGN_CENTER)
        addText("--------------------------------")
        addCommand(ESCPOSCommands.LF)

        // Blank line
        addCommand(ESCPOSCommands.LF)

        // Thank you message
        addCommand(ESCPOSCommands.ALIGN_CENTER)
        addText("Thank you for your business!")
        addCommand(ESCPOSCommands.LF)

        // Feed paper for cutting (3 blank lines)
        addFeed(3)

        // Convert all commands to single byte array
        val totalSize = commandBuffer.sumOf { it.size }
        val result = ByteArray(totalSize)
        var offset = 0
        for (bytes in commandBuffer) {
            System.arraycopy(bytes, 0, result, offset, bytes.size)
            offset += bytes.size
        }

        return result
    }

    private fun addCommand(command: ByteArray) {
        commandBuffer.add(command)
    }

    private fun addText(text: String) {
        commandBuffer.add(ESCPOSCommands.text(text))
    }

    private fun addFeed(lines: Int) {
        require(lines in 1..255) { "Feed lines must be between 1 and 255" }
        commandBuffer.add(ESCPOSCommands.feed(lines))
    }
}

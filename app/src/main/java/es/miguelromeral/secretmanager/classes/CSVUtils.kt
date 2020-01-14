package es.miguelromeral.secretmanager.classes

// By https://github.com/rogerta/secrets-for-android/

/**
Copyright 2005 Bytecode Pty Ltd.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

/*
 * The code copied from http://opencsv.sourceforge.net/
 *
 * While incorporating into secrets, the following changes were made:
 *
 * - removed the following methods to keep the bytecode smaller:
 *   writeAll(), all methods related to sql
 */

import java.io.*

/**
 * A very simple CSV writer released under a commercial-friendly license.
 *
 * @author Glen Smith
 */
class CSVWriter
/**
 * Constructs CSVWriter with supplied separator, quote char, escape char and line ending.
 *
 * @param writer
 * the writer to an underlying CSV source.
 * @param separator
 * the delimiter to use for separating entries
 * @param quotechar
 * the character to use for quoted elements
 * @param escapechar
 * the character to use for escaping quotechars or escapechars
 * @param lineEnd
 * the line feed terminator to use
 */
@JvmOverloads constructor(
    writer: Writer,
    private val separator: Char = DEFAULT_SEPARATOR,
    private val quotechar: Char = DEFAULT_QUOTE_CHARACTER,
    private val escapechar: Char = DEFAULT_ESCAPE_CHARACTER,
    private val lineEnd: String = DEFAULT_LINE_END
) {

    private val pw: PrintWriter

    init {
        this.pw = PrintWriter(writer)
    }

    /**
     * Writes the next line to the file.
     *
     * @param nextLine
     * a string array with each comma-separated element as a separate
     * entry.
     */
    fun writeNext(nextLine: Array<String>?) {

        if (nextLine == null)
            return

        val sb = StringBuffer()
        for (i in nextLine.indices) {

            if (i != 0) {
                sb.append(separator)
            }

            val nextElement = nextLine[i] ?: continue
            if (quotechar != NO_QUOTE_CHARACTER)
                sb.append(quotechar)
            for (j in 0 until nextElement.length) {
                val nextChar = nextElement[j]
                if (escapechar != NO_ESCAPE_CHARACTER && nextChar == quotechar) {
                    sb.append(escapechar).append(nextChar)
                } else if (escapechar != NO_ESCAPE_CHARACTER && nextChar == escapechar) {
                    sb.append(escapechar).append(nextChar)
                } else {
                    sb.append(nextChar)
                }
            }
            if (quotechar != NO_QUOTE_CHARACTER)
                sb.append(quotechar)
        }

        sb.append(lineEnd)
        pw.write(sb.toString())

    }

    /**
     * Flush underlying stream to writer.
     *
     * @throws IOException if bad things happen
     */
    @Throws(IOException::class)
    fun flush() {

        pw.flush()

    }

    /**
     * Close the underlying stream writer flushing any buffered content.
     *
     * @throws IOException if bad things happen
     */
    @Throws(IOException::class)
    fun close() {
        pw.flush()
        pw.close()
    }

    companion object {

        /** The character used for escaping quotes.  */
        val DEFAULT_ESCAPE_CHARACTER = '"'

        /** The default separator to use if none is supplied to the constructor.  */
        val DEFAULT_SEPARATOR = ','

        /**
         * The default quote character to use if none is supplied to the
         * constructor.
         */
        val DEFAULT_QUOTE_CHARACTER = '"'

        /** The quote constant to use when you wish to suppress all quoting.  */
        val NO_QUOTE_CHARACTER = '\u0000'

        /** The escape constant to use when you wish to suppress all escaping.  */
        val NO_ESCAPE_CHARACTER = '\u0000'

        /** Default line terminator uses platform encoding.  */
        val DEFAULT_LINE_END = "\n"
    }

}
/**
 * Constructs CSVWriter using a comma for the separator.
 *
 * @param writer
 * the writer to an underlying CSV source.
 */


/**
Copyright 2005 Bytecode Pty Ltd.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

/*
 * The code copied from http://opencsv.sourceforge.net/
 *
 * While incorporating into secrets, the following changes were made:
 *
 * - Added support of generics
 * - removed the following methods to keep the bytecode smaller:
 *   readAll(), some constructors
 */

/**
 * A very simple CSV reader released under a commercial-friendly license.
 *
 * @author Glen Smith
 */
class CSVReader
/**
 * Constructs CSVReader with supplied separator and quote char.
 *
 * @param reader
 * the reader to an underlying CSV source.
 * @param separator
 * the delimiter to use for separating entries
 * @param quotechar
 * the character to use for quoted elements
 * @param line
 * the line number to skip for start reading
 */
@JvmOverloads constructor(
    reader: Reader,
    private val separator: Char = DEFAULT_SEPARATOR,
    private val quotechar: Char = DEFAULT_QUOTE_CHARACTER,
    private val skipLines: Int = DEFAULT_SKIP_LINES
) {

    private val br: BufferedReader

    private var hasNext = true

    private var linesSkiped: Boolean = false

    /**
     * Reads the next line from the file.
     *
     * @return the next line from the file without trailing newline
     * @throws IOException
     * if bad things happen during the read
     */
    private val nextLine: String?
        @Throws(IOException::class)
        get() {
            if (!this.linesSkiped) {
                for (i in 0 until skipLines) {
                    br.readLine()
                }
                this.linesSkiped = true
            }
            val nextLine = br.readLine()
            if (nextLine == null) {
                hasNext = false
            }
            return if (hasNext) nextLine else null
        }

    init {
        this.br = BufferedReader(reader)
    }

    /**
     * Reads the next line from the buffer and converts to a string array.
     *
     * @return a string array with each comma-separated element as a separate
     * entry.
     *
     * @throws IOException
     * if bad things happen during the read
     */
    @Throws(IOException::class)
    fun readNext(): Array<String>? {

        val nextLine = nextLine
        return if (hasNext) parseLine(nextLine) else null
    }

    /**
     * Parses an incoming String and returns an array of elements.
     *
     * @param nextLine
     * the string to parse
     * @return the comma-tokenized list of elements, or null if nextLine is null
     * @throws IOException if bad things happen during the read
     */
    @Throws(IOException::class)
    private fun parseLine(nextLine: String?): Array<String>? {
        var nextLine: String? = nextLine ?: return null

        val tokensOnThisLine = ArrayList<String>()
        var sb = StringBuffer()
        var inQuotes = false
        do {
            if (inQuotes) {
                // continuing a quoted section, reappend newline
                sb.append("\n")
                nextLine = nextLine
                if (nextLine == null)
                    break
            }
            var i = 0
            while (i < nextLine!!.length) {

                val c = nextLine[i]
                if (c == quotechar) {
                    // this gets complex... the quote may end a quoted block, or escape another quote.
                    // do a 1-char lookahead:
                    if (inQuotes  // we are in quotes, therefore there can be escaped quotes in here.

                        && nextLine.length > i + 1  // there is indeed another character to check.

                        && nextLine[i + 1] == quotechar
                    ) { // ..and that char. is a quote also.
                        // we have two quote chars in a row == one quote char, so consume them both and
                        // put one on the token. we do *not* exit the quoted text.
                        sb.append(nextLine[i + 1])
                        i++
                    } else {
                        inQuotes = !inQuotes
                        // the tricky case of an embedded quote in the middle: a,bc"d"ef,g
                        if (i > 2 //not on the begining of the line

                            && nextLine[i - 1] != this.separator //not at the begining of an escape sequence

                            && nextLine.length > i + 1 &&
                            nextLine[i + 1] != this.separator //not at the	end of an escape sequence
                        ) {
                            sb.append(c)
                        }
                    }
                } else if (c == separator && !inQuotes) {
                    tokensOnThisLine.add(sb.toString())
                    sb = StringBuffer() // start work on next token
                } else {
                    sb.append(c)
                }
                i++
            }
        } while (inQuotes)
        tokensOnThisLine.add(sb.toString())
        return tokensOnThisLine.toTypedArray()

    }

    /**
     * Closes the underlying reader.
     *
     * @throws IOException if the close fails
     */
    @Throws(IOException::class)
    fun close() {
        br.close()
    }

    companion object {

        /** The default separator to use if none is supplied to the constructor.  */
        val DEFAULT_SEPARATOR = ','

        /**
         * The default quote character to use if none is supplied to the
         * constructor.
         */
        val DEFAULT_QUOTE_CHARACTER = '"'

        /**
         * The default line to start reading.
         */
        val DEFAULT_SKIP_LINES = 0
    }

}
/**
 * Constructs CSVReader using a comma for the separator.
 *
 * @param reader
 * the reader to an underlying CSV source.
 */
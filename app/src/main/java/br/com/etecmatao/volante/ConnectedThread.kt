package br.com.etecmatao.volante

import android.widget.Toast
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class ConnectedThread(socket: BluetoothSocket) : Thread() {
    private val mmInStream: InputStream?
    private val mmOutStream: OutputStream?

    private val Tag = "VOLANTE"

    init {
        socket.connect()

        var tmpIn: InputStream? = null
        var tmpOut: OutputStream? = null

        try {
            //Create I/O streams for connection
            tmpIn = socket.inputStream
            tmpOut = socket.outputStream
        } catch (e: IOException) {
        }

        mmInStream = tmpIn
        mmOutStream = tmpOut
    }

    override fun run() {
        val buffer = ByteArray(256)
        var bytes: Int

        // Keep looping to listen for received messages
        while (true) {
            try {
                bytes = mmInStream!!.read(buffer)            //read bytes from input buffer
                val readMessage = String(buffer, 0, bytes)
                Log.i(Tag, readMessage)
            } catch (e: IOException) {
                break
            }
        }
    }

    //write method
    fun write(input: String) {
        //converts entered String into bytes
        val msgBuffer = input.toByteArray()

        try {
            //write bytes over BT connection via outstream
            mmOutStream!!.write(msgBuffer)
        } catch (e: IOException) {
            Log.e(Tag, e.message, e)
        }
    }
}
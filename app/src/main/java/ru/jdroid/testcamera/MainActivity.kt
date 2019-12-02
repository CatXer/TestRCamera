package ru.jdroid.testcamera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        photo.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val mainScope = CoroutineScope(Dispatchers.Main)
                val allBytes = takePhoto()

                if (allBytes != "") {
                    val byteArray: ByteArray =
                        android.util.Base64.decode(allBytes, android.util.Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    mainScope.launch {
                        camera.setImageBitmap(bitmap)
                    }
                } else {
                    mainScope.launch {
                        camera.setImageResource(R.drawable.ic_launcher_background)
                    }
                }
            }
        }
    }

    private fun takePhoto(): String {
        var data = ""
        try {

            val s = Socket("jdroid.ru", 2000)
            val reader = Scanner(s.getInputStream())
            val writer = BufferedWriter(OutputStreamWriter(s.getOutputStream()))
            Log.d("TagM", "server: ${reader.nextLine()}")

            writer.write("[{\"client_type\":\"0\", \"method\":\"login\", \"login\":\"example@gmail.com\", \"password\":\"mypassword\"}]\n")
            writer.flush()
            Log.d("TagM", "server: ${reader.nextLine()}")

            /*

        optional

        writer.write("[{\"method\":\"add_device\", \"device_id\":\"3\"}]\n")
        writer.flush()
        println("server: ${reader.nextLine()}")

        writer.write("[{\"method\":\"get_devices\"}]\n")
        writer.flush()
        println("server: ${reader.nextLine()}")

        writer.write("[{\"method\":\"get_device\", \"device_id\":\"2\"}]\n")
        writer.flush()
        println("server: ${reader.nextLine()}")

        */

            writer.write("[{\"method\":\"sent_to_device\", \"device_id\":\"4\", \"command_id\":\"6\", \"arg\":\"1\"}]\n")
            writer.flush()
            Log.d("TagM", "server: ${reader.nextLine()}")

            data = reader.nextLine()

            writer.write("[{\"method\":\"disconnect\"}]\n")
            writer.flush()


            reader.close()
            writer.close()
            s.close()
        } catch (e: NoSuchElementException) {
        }

        return data
    }
}

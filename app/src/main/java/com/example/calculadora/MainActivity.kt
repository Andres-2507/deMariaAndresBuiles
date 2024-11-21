package com.example.calculadora

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private val SUMA = "+"
    private val RESTA = "-"
    private val MULTIPLICACION = "*"
    private val DIVISION = "/"
    private val PORCENTAJE = "%"
    private val POTENCIA = "^"
    private val RAIZ = "√"

    private var operacionActual = ""
    private var primerNumero: Double = Double.NaN
    private var segundoNumero: Double = Double.NaN

    private lateinit var tvTemp: TextView
    private lateinit var tvResult: TextView
    private lateinit var formatoDecimal: DecimalFormat
    private val historialOperaciones = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        formatoDecimal = DecimalFormat("#.##########")
        tvTemp = findViewById(R.id.tvTemp)
        tvResult = findViewById(R.id.tvResult)
    }

    fun cambiarOperador(b: View) {
        if (tvTemp.text.isNotEmpty() || primerNumero.toString() != "NaN") {
            calcular()
            val boton: Button = b as Button
            operacionActual = when (boton.text.toString().trim()) {
                "÷" -> DIVISION
                "x" -> MULTIPLICACION
                "^" -> POTENCIA
                "√" -> RAIZ
                else -> boton.text.toString().trim()
            }
            tvResult.text = formatoDecimal.format(primerNumero) + operacionActual
            tvTemp.text = ""
        }
    }

    fun calcular() {
        try {
            if (operacionActual == RAIZ) {
                val numero = tvTemp.text.toString().toDouble()
                primerNumero = kotlin.math.sqrt(numero)
                tvResult.text = formatoDecimal.format(primerNumero)
                tvTemp.text = ""
                operacionActual = ""
                return
            }

            if (!primerNumero.isNaN()) {
                if (tvTemp.text.toString().isEmpty()) {
                    tvTemp.text = tvResult.text.toString()
                }
                segundoNumero = tvTemp.text.toString().toDouble()
                tvTemp.text = ""

                primerNumero = when (operacionActual) {
                    SUMA -> primerNumero + segundoNumero
                    RESTA -> primerNumero - segundoNumero
                    MULTIPLICACION -> primerNumero * segundoNumero
                    DIVISION -> primerNumero / segundoNumero
                    PORCENTAJE -> primerNumero % segundoNumero
                    POTENCIA -> Math.pow(primerNumero, segundoNumero)
                    else -> primerNumero
                }
            } else {
                primerNumero = tvTemp.text.toString().toDouble()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun seleccionarNumero(b: View) {
        val boton: Button = b as Button
        tvTemp.text = tvTemp.text.toString() + boton.text.toString()
    }

    fun igual(b: View) {
        calcular()
        val resultado = formatoDecimal.format(primerNumero)
        tvResult.text = resultado

        // Guardar la operación en el historial
        if (operacionActual.isNotEmpty() || tvTemp.text.isNotEmpty()) {
            val operacion = "${tvResult.text} $operacionActual ${tvTemp.text} = $resultado"
            historialOperaciones.add(operacion)
        }

        operacionActual = ""
    }

    fun borrar(b: View) {
        val boton: Button = b as Button
        when (boton.text.toString().trim()) {
            "C" -> {
                if (tvTemp.text.toString().isNotEmpty()) {
                    val datosActuales: CharSequence = tvTemp.text
                    tvTemp.text = datosActuales.subSequence(0, datosActuales.length - 1)
                } else {
                    primerNumero = Double.NaN
                    segundoNumero = Double.NaN
                    tvTemp.text = ""
                    tvResult.text = ""
                }
            }
            "CA" -> {
                primerNumero = Double.NaN
                segundoNumero = Double.NaN
                tvTemp.text = ""
                tvResult.text = ""
            }
        }
    }
    fun mostrarHistorial(b: View) {
        if (historialOperaciones.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Historial")
                .setMessage("No hay operaciones registradas.")
                .setPositiveButton("Cerrar", null)
                .show()
            return
        }

        val historialTexto = historialOperaciones.joinToString(separator = "\n")
        AlertDialog.Builder(this)
            .setTitle("Historial de Operaciones")
            .setMessage(historialTexto)
            .setPositiveButton("Cerrar", null)
            .setNegativeButton("Borrar Historial") { _, _ ->
                historialOperaciones.clear()
            }
            .show()
    }
}

package com.test.pokedex.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import com.test.pokedex.R
import kotlinx.android.synthetic.main.activity_detail.*

class ActivityDetail: AppCompatActivity() {

    //Declaracion de variables para obtener datos del detail del pokemon
    private lateinit var name: TextView
    private lateinit var number: TextView
    private lateinit var types: TextView
    private lateinit var stats: TextView
    private lateinit var moves: TextView

    //Declaracion de variable no nula url
    var url : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        //Obtencion de elementos por id y asifnacion a variables prev. declaradas
        name = findViewById(R.id.pokemon_name)
        number = findViewById(R.id.pokemon_number)
        types = findViewById(R.id.pokemon_types)
        stats = findViewById(R.id.pokemon_stats)
        moves = findViewById(R.id.pokemon_moves)

        //Obtener URL
        val bundle :Bundle ?=intent.extras
        url = bundle!!.getString("url").toString()


        //Mandar a Initializer
        initializeData()
    }
    //Initializer
    fun initializeData(){
        //Recabar informacion de URL y API
        Ion.with(this)
            .load(url)
            .asJsonObject()
            .done { e, result ->
                if(e == null){
                    //Quitar las comillas que trae el nombre por la API y que empiece con mayuscula
                    name.text = result.get("name").toString().replace("\"","").capitalize()
                    number.text = result.get("id").toString()
                    //Traerlos como array porque pueden ser mas de 1
                    types.text = getTyping(result.get("types").asJsonArray)
                    stats.text = getStats(result.get("stats").asJsonArray)
                    moves.text = getMoves(result.get("moves").asJsonArray)

                    //Si no puedes jalar el Spirte de la API, mandas generico. Si puedes, agarras el sprite bueno.
                    if(!result.get("sprites").isJsonNull){
                        if(result.get("sprites").asJsonObject.get("front_default") != null){
                            Log.i("Salida", result.get("sprites").asJsonObject.get("front_default").asString)

                            //API Glide para cargar imagenes. https://bumptech.github.io/glide/
                            Glide
                                .with(this)
                                .load(result.get("sprites").asJsonObject.get("front_default").asString)
                                .placeholder(R.drawable.pokemon_logo_min)
                                .error(R.drawable.pokemon_logo_min)
                                .into(pokemon_image)

                            //Mandar logo de Pokemon si no se puede
                        }else{
                            pokemon_image.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pokemon_logo_min))
                        }

                    }else{
                        pokemon_image.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pokemon_logo_min))
                    }
                }
            }
    }

    //Obtener los tipos de PokeAPI
    fun getTyping(a: JsonArray): String{
        //Empezar string vacio para llenar con tipos
        var typing: String = ""

        //Loop para agarrar todos los tipos
        for(i in a){
            var x: JsonObject = i.asJsonObject.getAsJsonObject("type")
            typing = typing + x.get("name").asString.capitalize() + " \n"
        }

        return typing
    }

    //Obtener los stats de PokeAPI
    fun getStats(a: JsonArray): String{
        var stats: String = ""

        //Loop obtener nombre del stat y valor
        for(i in a){
            val stat = i.asJsonObject.get("base_stat").asString
            val nstat: JsonObject = i.asJsonObject.getAsJsonObject("stat")

            stats = stats + nstat.get("name").asString.capitalize() + ": " + stat + "\n"
        }

        return stats
    }

    fun getMoves(a: JsonArray): String{
        var moveset: String = ""

        //Loop obtener el moveset
        for(i in a){
            var x: JsonObject = i.asJsonObject.getAsJsonObject("move")
            moveset = moveset + x.get("name").asString.capitalize() + "\n"
        }

        return moveset
    }


}
package mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.ui.updatePet

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.databinding.FragmentUpdatePetBinding


class UpdatePetFragment : AppCompatActivity() {

    lateinit var binding: FragmentUpdatePetBinding
    var baseRemota = FirebaseFirestore.getInstance()
    var id_mascota = ""
    var curp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentUpdatePetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id_mascota = this.intent.extras!!.getString("idMascota")!!


        baseRemota
            .collection("MASCOTA")
            .document(id_mascota)
            .get()
            .addOnSuccessListener {
                binding.updatePetCurp.setText(it.getString("CURP"))
                binding.updatePetName.setText(it.getString("NOMBRE"))
                binding.uodatePetRace.setText(it.getString("NOMBRE"))
            }
            .addOnFailureListener {

                AlertDialog.Builder(this)
                    .setMessage("ERROR: ${it.message!!}")
                    .show()
            }


        binding.actualizar.setOnClickListener {
            /*
            try {
                mascota.nombre = binding.txtnombreMascota.text.toString()
                mascota.raza = binding.SpRaza.selectedItem.toString()
                mascota.curp = binding.txtcurp.text.toString()
            } catch (e:Exception) {
                mensaje("HAY CAMPOS VACIOS")
                return@setOnClickListener
            }


            try {
                if (!(mascota.curp == "" || mascota.nombre == "")) {
                    mascota.actualizar(id_mascota)
                    limpiarCampos()
                    finish()
                }
            }  catch(e:Exception) {
                mensaje("HAY CAMPOS VACIOS")
            }
            * */
        }
    }



    fun limpiarCampos() {
        binding.updatePetName.setText("")
        binding.uodatePetRace.setText("")
        binding.updatePetCurp.setText("")
    }
}
package mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.ui.register_pets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.R
import mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.databinding.FragmentRegisterPetBinding
import mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.databinding.FragmentUpdatePetBinding
import mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.ui.updatePet.UpdatePetFragment

class RegisterPetsFragment : Fragment() {
    var baseRemota = FirebaseFirestore.getInstance()
    var listaIDP = ArrayList<String>()
    val arregloP = ArrayList<String>()
    var listaIDM = ArrayList<String>()
    val arregloM = ArrayList<String>()
    private var _binding: FragmentRegisterPetBinding? = null


    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterPetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        baseRemota.collection("MASCOTA").addSnapshotListener { query, error ->
            if(error!=null){
                //si hubo error!!!
                AlertDialog.Builder(requireContext())
                    .setMessage(error.message)
                    .show()
                return@addSnapshotListener
            }
            arregloM.clear()
            listaIDM.clear()
            for(documento in query!!){
                var cadena = "Nombre: ${documento.getString("NOMBRE")}\n" +
                        "Raza: ${documento.getString("RAZA")}\n"
                arregloM.add(cadena)
                listaIDM.add(documento.id.toString())
            }
            binding.listaMascotas.adapter = ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_list_item_1,arregloM)
            binding.listaMascotas.setOnItemClickListener { adapterView, view, index, l ->
                dialogoEliminarActualiza(index)
            }
        }

        val spinner: Spinner = binding.SpRaza
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.razas,
            androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        binding.insertarMascota.setOnClickListener {
            val datos = hashMapOf(
                "NOMBRE" to binding.txtnombreMascota.text.toString(),
                "RAZA" to binding.SpRaza.selectedItem.toString(),
                "CURP" to binding.txtcurp.text.toString()

            )
            baseRemota.collection("MASCOTA")
                .add(datos)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(),"EXITO! SI SE INSERTO", Toast.LENGTH_LONG)
                        .show()
                    limpiarCampos()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(requireContext())
                        .setMessage(it.message!!)
                        .show()
                }
        }

        binding.btnbuscar.setOnClickListener {
            var buscar = binding.txtbuscarPropietario.text.toString()
            buscarPropietario(buscar)
        }
        binding.btnLimpiar.setOnClickListener {
            limpiarCampos()
        }
            return root
    }
    private fun dialogoEliminarActualiza(posicion :Int) {
        var idSeleccionar = listaIDM.get(posicion)

        AlertDialog.Builder(requireContext())
            .setTitle("Atención!!")
            .setMessage("¿QUÉ DESEAS HACER CON\n${arregloM.get(posicion)}?")
            .setPositiveButton("ELIMINAR") {d,i ->
                eliminarMascota(idSeleccionar)
            }
            .setNeutralButton("ACTUALIZAR") {d,i ->

            }
            .setNegativeButton("CANCELAR") {d,i -> }
            .show()
    }

    private fun eliminarMascota(idSeleccionar: String) {
        baseRemota
            .collection("MASCOTA")
            .document(idSeleccionar)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"SE ELIMINO CON EXITO", Toast.LENGTH_LONG)
                    .show()
            }
            .addOnFailureListener {
                AlertDialog.Builder(requireContext())
                    .setMessage("ERROR: ${it.message!!}")
                    .show()
            }
    }

    private fun buscarPropietario(buscar: String) {
        //Evento ( se dispara solito) para llenar propietarios
        baseRemota.collection("PROPIETARIO")
            .addSnapshotListener { query, error ->
            if(error!=null){
                //si hubo error!!!
                AlertDialog.Builder(requireContext())
                    .setMessage(error.message)
                    .show()

                return@addSnapshotListener
            }
            arregloP.clear()
            listaIDP.clear()
            for(documento in query!!){
                var cadena = "Nombre: ${documento.getString("NOMBRE")}\n" +
                        "Telefono: ${documento.getString("TELEFONO")}\n" +
                        "Edad: ${documento.getLong("EDAD")}"
                arregloP.add(cadena)
                listaIDP.add(documento.id.toString())
            }
            binding.ownerList.adapter = ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_list_item_1,arregloP)
            binding.ownerList.setOnItemClickListener { adapterView, view, index, l ->
                var idSeleccionar = listaIDP.get(index)

                baseRemota
                    .collection("PROPIETARIO")
                    .document(idSeleccionar)
                    .get()
                    .addOnSuccessListener {
                        binding.txtcurp.setText(it.getString("CURP"))
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(requireContext())
                            .setMessage("ERROR: ${it.message!!}")
                            .show()

                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun limpiarCampos() {
        binding.txtbuscarPropietario.setText("")
        binding.txtcurp.setText("")
        binding.txtnombreMascota.setText("")
    }

    override fun onResume() {
        super.onResume()
    }
}

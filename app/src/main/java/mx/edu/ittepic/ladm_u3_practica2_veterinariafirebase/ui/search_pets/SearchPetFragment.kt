package mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.ui.search_pets

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
import mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.databinding.FragmentSearchPetsBinding
import mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.ui.updatePet.UpdatePetFragment

class SearchPetFragment : Fragment() {
    var baseRemota = FirebaseFirestore.getInstance()
    var listaID = ArrayList<String>()
    val arreglo = ArrayList<String>()
    private var _binding: FragmentSearchPetsBinding? = null

    private val binding get() = _binding!!

    var listaIDs = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchPetsBinding.inflate(inflater, container, false)
        baseRemota.collection("MASCOTA").addSnapshotListener { query, error ->
            if(error!=null){
                //si hubo error!!!
                AlertDialog.Builder(requireContext())
                    .setMessage(error.message)
                    .show()
                return@addSnapshotListener
            }
            arreglo.clear()
            listaID.clear()
            for(documento in query!!){
                var cadena = "Nombre: ${documento.getString("NOMBRE")}\n" +
                        "Raza: ${documento.getString("RAZA")}\n"
                arreglo.add(cadena)
                listaID.add(documento.id.toString())
            }
            binding.pets.adapter = ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_list_item_1,arreglo)
            binding.pets.setOnItemClickListener { adapterView, view, index, l ->
                dialogoEliminaActualiza(index)
            }
        }
        val root: View = binding.root

        val spinner: Spinner = binding.SpConsultasMascotas
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.buscarMascota,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }


        binding.btnBuscar.setOnClickListener {
            var busqueda = binding.buscarMascota.text.toString()
            mostrarFiltro(busqueda,binding.SpConsultasMascotas.selectedItem.toString())
        }

        return  root
    }
    fun mostrarFiltro(busqueda:String,filtro:String) {
        baseRemota.collection("MASCOTA")
            .whereEqualTo("${filtro}", busqueda)
            .addSnapshotListener { query, error ->
                if (error != null) {
                    //SI HUBO UNA EXCEPCIÓN
                    AlertDialog.Builder(requireContext())
                        .setMessage(error.message!!)
                        .show()

                    return@addSnapshotListener
                }
                arreglo.clear()
                listaID.clear()
                for (documento in query!!) {
                    var cadena = "Nombre: ${documento.getString("NOMBRE")}\n" +
                            "Raza: ${documento.getString("RAZA")}\n"
                    arreglo.add(cadena)

                    listaID.add(documento.id.toString())
                }

                binding.pets.adapter = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    arreglo
                )
            }
    }
    private fun dialogoEliminaActualiza(index: Int) {
        var idElegido = listaID.get(index)

        AlertDialog.Builder(requireContext()).setTitle("ATENCION!").
        setMessage("¿Que deseas hacer con \n ${arreglo.get(index)}?")
            .setPositiveButton("ELIMINAR"){d,i -> eliminarMascota(idElegido)}
            .setNeutralButton("ACTUALIZAR") {d,i -> actualizar(idElegido)}
            .setNegativeButton("CANCELAR") {d,i ->}
            .show()
    }

    private fun actualizar(idElegido: String) {
        var otraVentana = Intent(requireActivity(),UpdatePetFragment::class.java)
        otraVentana.putExtra("idElegido", idElegido)
        startActivity(otraVentana)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}


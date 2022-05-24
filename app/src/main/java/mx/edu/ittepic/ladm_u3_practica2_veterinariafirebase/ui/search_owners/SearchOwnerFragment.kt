package mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.ui.search_owners

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
import mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.databinding.FragmentSearchOwnersBinding


class SearchOwnerFragment : Fragment() {
    var baseRemota = FirebaseFirestore.getInstance()
    var listaID = ArrayList<String>()
    val arreglo = ArrayList<String>()

    private var _binding: FragmentSearchOwnersBinding? = null

    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchOwnersBinding.inflate(inflater, container, false)

        baseRemota.collection("PROPIETARIO").addSnapshotListener { query, error ->
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
                        "Telefono: ${documento.getString("TELEFONO")}\n" +
                        "Edad: ${documento.getString("EDAD")}"
                arreglo.add(cadena)
                listaID.add(documento.id.toString())
            }
            binding.owners.adapter = ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_list_item_1,arreglo)
            binding.owners.setOnItemClickListener { adapterView, view, index, l ->
                dialogoEliminaActualiza(index)
            }
        }

        val root: View = binding.root

        val spinner: Spinner = binding.SpConsultasPropietario
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.buscarProPor,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }


        binding.btnBuscar.setOnClickListener {
            var busqueda = binding.txtbuscar.text.toString()
            mostrarFiltro(busqueda,binding.SpConsultasPropietario.selectedItem.toString())
        }

        return root
    }
    private fun dialogoEliminaActualiza(index: Int) {
        var idElegido = listaID.get(index)

        AlertDialog.Builder(requireContext()).setTitle("ATENCION!").
        setMessage("¿Que deseas hacer con \n ${arreglo.get(index)}?")
            .setPositiveButton("ELIMINAR"){d,i ->
                eliminarPropietario(idElegido)}
            .setNeutralButton("ACTUALIZAR") {d,i -> }
            .setNegativeButton("CANCELAR") {d,i ->}
            .show()
    }

    fun eliminarPropietario(idElegido: String) {
        baseRemota
            .collection("PROPIETARIO")
            .document(idElegido)
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

    fun mostrarFiltro(busqueda:String,filtro:String) {
        if (filtro != "EDAD") {
            baseRemota.collection("PROPIETARIO")
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
                                "Telefono: ${documento.getString("TELEFONO")}\n" +
                                "Edad: ${documento.getLong("EDAD")}"
                        arreglo.add(cadena)

                        listaID.add(documento.id.toString())
                    }

                    binding.owners.adapter = ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        arreglo
                    )
                }
        } else if (filtro == "EDAD") {
            baseRemota.collection("PROPIETARIO")
                .whereEqualTo("${filtro}", busqueda.toInt())
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
                                "Telefono: ${documento.getString("TELEFONO")}\n" +
                                "Edad: ${documento.getLong("EDAD")}"
                        arreglo.add(cadena)

                        listaID.add(documento.id.toString())
                    }

                    binding.owners.adapter = ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        arreglo
                    )
                }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}



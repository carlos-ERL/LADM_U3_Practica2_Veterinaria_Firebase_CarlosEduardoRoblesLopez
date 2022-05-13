package mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.ui.register_owners

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.ittepic.ladm_u3_practica2_veterinariafirebase.databinding.FragmentRegisterOwnerBinding

class RegisterOwnerFragment : Fragment() {
    private var _binding: FragmentRegisterOwnerBinding? = null
    var listaIDs = ArrayList<String>()
    var curp = ""
    var updateFlag = 0

    var baseRemota = FirebaseFirestore.getInstance()
    var listaID = ArrayList<String>()
    val arreglo = ArrayList<String>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterOwnerBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
                        "Edad: ${documento.getLong("EDAD")}"
                arreglo.add(cadena)
                listaID.add(documento.id.toString())
            }
            binding.owners.adapter = ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_list_item_1,arreglo)
            binding.owners.setOnItemClickListener { adapterView, view, index, l ->
                dialogoEliminaActualiza(index)
            }
        }
        binding.insertar.setOnClickListener {

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun dialogoEliminaActualiza(index: Int) {
        var idElegido = listaID.get(index)

        AlertDialog.Builder(requireContext()).setTitle("ATENCION!").
        setMessage("¿Que deseas hacer con \n ${arreglo.get(index)}?")
            .setPositiveButton("ELIMINAR"){d,i -> }
            .setNeutralButton("ACTUALIZAR") {d,i -> }
            .setNegativeButton("CANCELAR") {d,i ->}
            .show()
    }

    fun limpiarCampos() {
        binding.ownerCurp.setText("")
        binding.ownerName.setText("")
        binding.ownerPhone.setText("")
        binding.ownerAge.setText("")
        binding.insertar.setHint("REGISTRAR")
        binding.insertar.setText("REGISTRAR")
        updateFlag = 0

    }

    override fun onResume() {
        super.onResume()

    }
}


package br.com.etecmatao.biblioteca.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.etecmatao.biblioteca.R
import br.com.etecmatao.biblioteca.inflate
import br.com.etecmatao.biblioteca.model.Livro
import kotlinx.android.synthetic.main.item_view_livro_list.view.*

class LivroAdapter : RecyclerView.Adapter<LivroAdapter.LivroViewHolder>(){
    private val list: ArrayList<Livro> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder = LivroViewHolder(parent.inflate(R.layout.item_view_livro_list))

    override fun getItemCount(): Int = list.count()

    override fun onBindViewHolder(holder: LivroViewHolder, position: Int) {
        var item = list[position]
        holder.bind(item)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun addAll(livros: List<Livro>){
        list.addAll(livros)
        notifyDataSetChanged()
    }

    inner class LivroViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(item: Livro) = with(itemView){
            txtTitulo.text = item.titulo
            txtAutor.text = item.autor
            txtAnoLancamento.text = item.anoLancamento.toString()
            txtQuantidadePaginas.text = item.qtdePaginas.toString()
        }
    }
}
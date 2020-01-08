package es.miguelromeral.secretmanager.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.database.Secret
import es.miguelromeral.secretmanager.databinding.ItemSecretBinding
import es.miguelromeral.secretmanager.ui.listeners.DecryptSecretListener
import es.miguelromeral.secretmanager.ui.listeners.RemoveSecretListener


class SecretAdapter(
    val decryptListener: DecryptSecretListener,
    val removeListener: RemoveSecretListener) : ListAdapter<Secret, SecretAdapter.ViewHolder>(SecretDiffCallback())
{
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, decryptListener, removeListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }



    class ViewHolder private constructor(val binding: ItemSecretBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Secret, decryptListener: DecryptSecretListener, removeListener: RemoveSecretListener){
            val resources = itemView.context.resources
            binding.item = item
            binding.decryptListener = decryptListener
            binding.removeListener = removeListener
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSecretBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }



    class SecretDiffCallback : DiffUtil.ItemCallback<Secret>() {
        override fun areItemsTheSame(oldItem: Secret, newItem: Secret): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Secret, newItem: Secret): Boolean {
            return oldItem == newItem
        }
    }

}
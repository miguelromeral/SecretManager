package es.miguelromeral.secretmanager.ui.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.database.Secret
import es.miguelromeral.secretmanager.databinding.ItemSecretBinding
import es.miguelromeral.secretmanager.network.ServiceQR
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

            // Three dots menu for each item
            binding.tvThreeDots.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.inflate(R.menu.option_menu)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.option_show_secret -> {
                            decryptListener.onClick(item)
                            true
                        }
                        R.id.option_delete_secret -> {
                            removeListener.onClick(item)
                            true
                        }
                        R.id.option_get_qr -> {
                            ServiceQR.openQRIntent(itemView.context, item.content)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }

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
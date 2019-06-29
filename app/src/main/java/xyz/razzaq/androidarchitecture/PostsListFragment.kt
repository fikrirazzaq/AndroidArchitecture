package xyz.razzaq.androidarchitecture


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import xyz.razzaq.androidarchitecture.databinding.FragmentPostsListBinding
import xyz.razzaq.androidarchitecture.databinding.ListItemPostBinding


class PostsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentPostsListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_posts_list, container, false
        )

        setClickListenerCreatePost(binding)

        return binding.root
    }

    private fun setClickListenerCreatePost(binding: FragmentPostsListBinding) {
        binding.fabAddPost.setOnClickListener { v: View ->
            v.findNavController().navigate(PostsListFragmentDirections.actionPostsListFragmentToCreatePostFragment())
        }
    }
}

class PostViewHolder(val viewDataBinding: ListItemPostBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_item_post
    }
}

class PostAdapter() : RecyclerView.Adapter<PostViewHolder>() {

    var posts: List<Post> = emptyList()
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val withDataBinding: ListItemPostBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            PostViewHolder.LAYOUT,
            parent,
            false
        )
        return PostViewHolder(withDataBinding)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.viewDataBinding.also {
            //Bind post item
        }
    }

}
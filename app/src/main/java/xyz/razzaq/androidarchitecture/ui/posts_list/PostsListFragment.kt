package xyz.razzaq.androidarchitecture.ui.posts_list


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.razzaq.androidarchitecture.R
import xyz.razzaq.androidarchitecture.databinding.FragmentPostsListBinding
import xyz.razzaq.androidarchitecture.databinding.ListItemPostBinding
import xyz.razzaq.androidarchitecture.domain.Post
import xyz.razzaq.androidarchitecture.ui.PostsListFragmentDirections
import xyz.razzaq.androidarchitecture.viewmodel.PostsViewModel


class PostsListFragment : Fragment() {

    private val viewModel: PostsViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProviders.of(this, PostsViewModel.Factory(activity.application))
            .get(PostsViewModel::class.java)
    }

    private var viewModelAdapter: PostAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.allPost.observe(viewLifecycleOwner, Observer<List<Post>> {
            it?.apply {
                viewModelAdapter?.posts = it
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentPostsListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_posts_list, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModelAdapter =
            PostAdapter(PostClick {
                findNavController().navigate(
                    PostsListFragmentDirections.actionPostsListFragmentToPostDetailFragment(
                        it
                    )
                )
            })

        binding.root.findViewById<RecyclerView>(R.id.rcvPosts).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

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

class PostAdapter(private val callback: PostClick) : RecyclerView.Adapter<PostViewHolder>() {

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
            it.post = posts[position]
            it.postCallback = callback
        }
    }
}

class PostClick(val block: (Post) -> Unit) {
    fun onClick(post: Post) = block(post)
}
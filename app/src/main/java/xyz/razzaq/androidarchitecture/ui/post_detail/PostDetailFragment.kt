package xyz.razzaq.androidarchitecture.ui.post_detail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.razzaq.androidarchitecture.R
import xyz.razzaq.androidarchitecture.databinding.FragmentPostDetailBinding
import xyz.razzaq.androidarchitecture.databinding.ListItemCommentBinding
import xyz.razzaq.androidarchitecture.domain.Comment
import xyz.razzaq.androidarchitecture.viewmodel.PostsViewModel

class PostDetailFragment : Fragment() {

    private val viewModel: PostsViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProviders.of(this, PostsViewModel.Factory(activity.application)).get(PostsViewModel::class.java)
    }

    private var viewModelAdapter: CommentAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val args = PostDetailFragmentArgs.fromBundle(arguments!!)
        val binding: FragmentPostDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_detail, container, false)

        binding.post = args.post
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.getComments(args.post.id).observe(viewLifecycleOwner, Observer<List<Comment>> {
            for (comment in it) {
                it?.apply {
                    viewModelAdapter?.comments = it
                }
            }
        })

        viewModelAdapter = CommentAdapter()

        binding.root.findViewById<RecyclerView>(R.id.rcvComments).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        return binding.root
    }
}

class CommentViewHolder(val viewDataBinding: ListItemCommentBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_item_comment
    }
}

class CommentAdapter : RecyclerView.Adapter<CommentViewHolder>() {

    var comments: List<Comment> = emptyList()
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val withDataBinding: ListItemCommentBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                CommentViewHolder.LAYOUT, parent, false)
        return CommentViewHolder(withDataBinding)
    }

    override fun getItemCount(): Int = comments.size


    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.comment = comments[position]
        }
    }
}

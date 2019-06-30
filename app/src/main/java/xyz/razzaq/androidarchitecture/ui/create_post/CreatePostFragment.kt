package xyz.razzaq.androidarchitecture.ui.create_post


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import xyz.razzaq.androidarchitecture.R
import xyz.razzaq.androidarchitecture.databinding.FragmentCreatePostBinding
import xyz.razzaq.androidarchitecture.viewmodel.PostsViewModel


class CreatePostFragment : Fragment() {

    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var toast: Toast

    private val viewModel: PostsViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProviders.of(this, PostsViewModel.Factory(activity.application))
            .get(PostsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_post, container, false
        )

        observeResultMessage(viewModel.resultMessage)
        observeIsLoaded(viewModel.isLoaded)

        setHasOptionsMenu(true)

        @SuppressLint("ShowToast")
        toast = Toast.makeText(activity, null, Toast.LENGTH_SHORT)

        return binding.root
    }

    private fun observeIsLoaded(isLoaded: MutableLiveData<Boolean>) {
        isLoaded.observe(this, Observer {
            if (it != null) {
                if (it) {
                    binding.progressPosting.visibility = View.GONE
                }
            }
        })
    }

    private fun observeResultMessage(resultMessage: MutableLiveData<String>) {
        resultMessage.observe(this, Observer {
            if (it != null) {
                if (it.toLowerCase().contains("success")) {
                    toast.setText(it)
                    toast.show()
                    hideSoftKeyboard()
                    findNavController().navigateUp()
                } else if (it.toLowerCase().contains("failed")) {
                    toast.setText(it)
                    toast.show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.create_post_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.send -> sendPost()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendPost() {
        if (binding.edtTitle.text!!.isEmpty() && binding.edtBody.text!!.isEmpty()) {
            toast.setText("Post cannot be empty.")
            toast.show()
        } else {
            binding.progressPosting.visibility = View.VISIBLE
            viewModel.addPost(
                binding.edtTitle.toString(),
                binding.edtBody.toString(),
                322
            )
        }
    }

    fun hideSoftKeyboard() {
        val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
    }

}

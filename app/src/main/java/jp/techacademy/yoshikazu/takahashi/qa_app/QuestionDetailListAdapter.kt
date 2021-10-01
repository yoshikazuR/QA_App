package jp.techacademy.yoshikazu.takahashi.qa_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.list_question_detail.view.*
import kotlinx.android.synthetic.main.list_question_detail_logined.*

class QuestionDetailListAdapter(context: Context, private val mQustion: Question) : BaseAdapter() {
    companion object {
        private val TYPE_QUESTION = 0
        private val TYPE_ANSWER = 1
    }

    private var mLayoutInflater: LayoutInflater? = null
    private var isFavorite = false
    private var titleFFlag = ""

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return 1 + mQustion.answers.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_QUESTION
        } else {
            TYPE_ANSWER
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Any {
        return mQustion
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view

        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                if(FirebaseAuth.getInstance().currentUser != null) {
                    convertView = mLayoutInflater!!.inflate(R.layout.list_question_detail_logined, parent, false)!!
                }else {
                    convertView =
                        mLayoutInflater!!.inflate(R.layout.list_question_detail, parent, false)!!
                }
            }
            val body = mQustion.body
            val name = mQustion.name

            val bodyTextView = convertView.bodyTextView as TextView
            bodyTextView.text = body

            val nameTextView = convertView.nameTextView as TextView
            nameTextView.text = name

            val bytes = mQustion.imageBytes
            if (bytes.isNotEmpty()) {
                val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
                val imageView = convertView.findViewById<View>(R.id.imageView) as ImageView
                imageView.setImageBitmap(image)
            }

            titleFFlag = mQustion.title

            var context = convertView.context

            val data = context.getSharedPreferences("favoriteFlags", Context.MODE_PRIVATE)
            isFavorite = data.getBoolean(mQustion.uid+"/"+titleFFlag,false)

            if (FirebaseAuth.getInstance().currentUser != null) {
                var favoriteImageView = convertView.findViewById<ImageView>(R.id.favoriteImageView)
                favoriteImageView.setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
                favoriteImageView.setOnClickListener {
                    val edit = data.edit()
                    if(isFavorite) {
                        isFavorite = false
                        edit.putBoolean(mQustion.uid+"/"+titleFFlag,false)
                    }else {
                        isFavorite = true
                        edit.putBoolean(mQustion.uid+"/"+titleFFlag,true)
                    }
                    edit.commit()
                    favoriteImageView.setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
                }
            }
        } else {
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_answer, parent, false)!!
            }

            val answer = mQustion.answers[position - 1]
            val body = answer.body
            val name = answer.name

            val bodyTextView = convertView.bodyTextView as TextView
            bodyTextView.text = body

            val nameTextView = convertView.nameTextView as TextView
            nameTextView.text = name
        }

        return convertView
    }
}

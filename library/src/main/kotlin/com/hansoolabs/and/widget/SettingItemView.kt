package com.hansoolabs.and.widget

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.hansoolabs.and.R

@Suppress("MemberVisibilityCanBePrivate", "unused")
/**
 * Simple Item View
 * Created by brownsoo on 2017. 10. 13..
 */

class SettingItemView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    LinearLayout(context, attrs, defStyle) {

    private val iconIv: ImageView
    private val titleTv: TextView
    private val descTv: TextView
    private val accessoryTv: TextView
    private val accessoryContainer: LinearLayout
    private val switch: SwitchCompat
    private val divider: View

    var title: CharSequence? = null
        set(value) {
            field = value
            titleTv.text = value
        }
    var description: CharSequence? = null
        set(value) {
            field = value
            descTv.text = value
            descTv.visibility = if (value != null) View.VISIBLE else View.GONE
        }

    var accessory: CharSequence? = null
        set(value) {
            field = value
            accessoryTv.text = value
            accessoryTv.visibility = if (value == null) View.GONE else View.VISIBLE
        }

    var switchVisible: Boolean = false
        set(value) {
            field = value
            switch.visibility = if (value) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

    var isChecked: Boolean = false
        set(value) {
            field = value
            switch.isChecked = value
        }

    var titleColor: Int = Color.parseColor("#0d131b")
        set(value) {
            field = value
            titleTv.setTextColor(value)
        }
    var descriptionColor: Int = Color.parseColor("#0d131b")
        set(value) {
            field = value
            descTv.setTextColor(value)
        }

    var dividerColor: Int =  Color.parseColor("#b3b8c5")
        set(value) {
            field = value
            divider.setBackgroundColor(value)
        }

    init {

        View.inflate(context, R.layout.and__setting_item_view, this)
        iconIv = findViewById(R.id.icon)
        titleTv = findViewById(R.id.title)
        descTv = findViewById(R.id.desc)
        accessoryTv = findViewById(R.id.accessory_tv)
        accessoryContainer = findViewById(R.id.accessory_container)
        switch = findViewById(R.id.tailSwitch)
        divider = findViewById(R.id.split_line)

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SettingItemView, defStyle, 0)
        setIcon(a.getResourceId(R.styleable.SettingItemView_itemIcon, -1))
        title = a.getString(R.styleable.SettingItemView_itemTitle)
        description = a.getString(R.styleable.SettingItemView_itemDescription)
        accessory = a.getString(R.styleable.SettingItemView_itemAccessory)
        switchVisible = a.getBoolean(R.styleable.SettingItemView_itemSwitch, false)
        isChecked = a.getBoolean(R.styleable.SettingItemView_itemChecked, false)
        a.getColor(R.styleable.SettingItemView_titleColor, -1).let {
            if (it > 0) titleColor = it
        }
        a.getColor(R.styleable.SettingItemView_descriptionColor, -1).let {
            if (it > 0) descriptionColor = it
        }
        a.getColor(R.styleable.SettingItemView_dividerColor, -1).let {
            if (it > 0) dividerColor = it
        }
        a.recycle()
    }

    fun setIconVisible(visible: Boolean) {
        iconIv.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setIcon(resId: Int) {
        setIconVisible(resId > 0)
        if (resId > 0) {
            iconIv.setImageDrawable(ContextCompat.getDrawable(context, resId))
        }
    }

    fun addAccessoryView(view: View) {
        accessoryContainer.addView(view)
    }

    fun addAccessoryView(view: View, at: Int) {
        accessoryContainer.addView(view, at)
    }
}
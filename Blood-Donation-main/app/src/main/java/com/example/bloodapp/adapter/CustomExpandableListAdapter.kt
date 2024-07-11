package com.example.bloodapp.ui.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.bloodapp.R
import java.util.HashMap

// BaseExpandableListAdapter sınıfını genişleten özel adaptör sınıfı
class CustomExpandableListAdapter(
    private val context: Context,
    private val listDataHeader: List<String>, // Grup başlıklarının listesi
    private val listDataChild: HashMap<String, List<String>> // Her grup için child verilerini içeren HashMap
) : BaseExpandableListAdapter() {

    // Belirtilen grup ve child konumundaki çocuk nesnesini döndürür
    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listDataChild[listDataHeader[groupPosition]]!![childPosition]
    }

    // Belirtilen grup ve child konumundaki child kimliğini döndürür
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    // Belirtilen gruptaki çocuk sayısını döndürür
    override fun getChildrenCount(groupPosition: Int): Int {
        return listDataChild[listDataHeader[groupPosition]]!!.size
    }

    // Belirtilen grup nesnesini döndürür
    override fun getGroup(groupPosition: Int): Any {
        return listDataHeader[groupPosition]
    }

    // Grup sayısını döndürür
    override fun getGroupCount(): Int {
        return listDataHeader.size
    }

    // Belirtilen grup kimliğini döndürür
    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    // Kimliklerin sabit olup olmadığını belirtir (sabit değil)
    override fun hasStableIds(): Boolean {
        return false
    }

    // Child öğelerinin seçilebilir olup olmadığını belirtir (seçilebilir)
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    // Grup görünümünü oluşturur veya geri dönüştürülmüş bir görünümü kullanır
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val headerTitle = getGroup(groupPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_group, parent, false)
        val lblListHeader = view.findViewById<TextView>(R.id.lblListHeader)
        lblListHeader.text = headerTitle
        return view
    }

    // Child görünümünü oluşturur veya geri dönüştürülmüş bir görünümü kullanır
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val childText = getChild(groupPosition, childPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        val txtListChild = view.findViewById<TextView>(R.id.lblListItem)
        txtListChild.text = childText
        return view
    }
}

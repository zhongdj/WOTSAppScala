package com.qa.gui.controller

import com.qa.data.entity.QueryLoader
import scalafx.scene.layout.VBox
import com.qa.gui.panel.DeliveryBar
import com.qa.data.entity.PurchaseOrder

/**
 * @author cboucher
 */
class DeliveryPanelController {
  def createTable(): VBox = {
    var table = new VBox
    val orders = QueryLoader.searchPurchaseOrder(new PurchaseOrder(null,null,null,null,null,null))
    def addRow(i: Int) {
      if(i < orders.size) {
        println(orders(i))
        table.children.add(new DeliveryBar(orders(i)))
        addRow(i +(1))
      }
    }
    if(orders != null) {
      addRow(0)
    }
    table
  }
}
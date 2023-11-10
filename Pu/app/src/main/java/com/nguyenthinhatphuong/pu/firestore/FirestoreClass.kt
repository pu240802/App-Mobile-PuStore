package com.nguyenthinhatphuong.pu.firestore
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nguyenthinhatphuong.pu.models.*
import com.nguyenthinhatphuong.pu.ui.ui.activites.*
import com.nguyenthinhatphuong.pu.ui.ui.fragments.*

import com.nguyenthinhatphuong.pu.utils.Constants


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: RegisterActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo,SetOptions.merge())
            .addOnSuccessListener{
                activity.userRegistrationSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,"Error while registering the user.",e
                )
            }
    }

    fun getCurrentUserID():String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
    fun getUserDetails(activity: Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName,document.toString())
                val user = document.toObject(User:: class.java)!!

                val sharedPreferences = activity.getSharedPreferences(Constants.PU_PREFERENCES,
                Context.MODE_PRIVATE)

                val editor: SharedPreferences.Editor = sharedPreferences.edit()

                //Key:logged_in_username
                //Value: Phuong Nguyen
                editor.putString(Constants.LOGGED_IN_USERNAME,"${user.firstName} ${user.lastName}")
                editor.apply()
                when(activity){
                    is LoginActivity ->{
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity ->{
                        activity.userDetailsSuccess(user)
                    }

                }
            }
            .addOnFailureListener { e ->
                when(activity){
                    is LoginActivity ->{
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity ->{
                        activity.hideProgressDialog()
                    }

                }
                Log.e(activity.javaClass.simpleName,
                "Error While getting user details.",
                e)
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity){
                    is UserProfileActivity ->{
                        activity.userProfileUpdateSuccess()

                    }
                }
            }
            .addOnFailureListener {
                    e->
                when(activity){
                    is UserProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,"Error While updating the user details.",
                    e
                )
            }



    }
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?,imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(activity, imageFileURI)

        )
        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.e("Downloadable Image URL", uri.toString())
                when (activity) {
                    is UserProfileActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }
                    is AddProductActivity ->{
                        activity.imageUploadSuccess(uri.toString())
                    }
                }
            }

        }
            .addOnFailureListener { exception ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }
    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product){
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while uploading the product details.",

                    e)
            }
    }

    fun getProductsList(fragment: Fragment){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when(fragment){
                    is ProductsFragment ->{
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }
    }

    fun getProductDeatails(activity: ProductDetailsActivity, productId: String){
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val product =document.toObject(Product::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }

            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Erroe while getting the product details.",e)
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem){
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
               activity.addToCartSuccess()

            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productId:String){
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()

            }
            .addOnFailureListener {
                    e->

                fragment.hideProgressDialog()
                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                "Error While deleting the product.",
                e
                )
            }

    }

    fun getCartList(activity: Activity){
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<CartItem> = ArrayList()

                for(i in document.documents){
                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }
                when(activity){
                    is CartListActivity ->{
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity ->{
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener {
                e->
                when (activity){
                    is CartListActivity ->{
                        activity.hideProgressDialog()
                    }

                    is CheckoutActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName,"Error while getting the cart list items.",e)
            }
    }

    fun updateAllDetails(activity: CheckoutActivity,cartList: ArrayList<CartItem>, order: Order){

        val writeBatch = mFireStore.batch()
        for (cartItem in cartList){


            val soldProduct = SoldProduct(
                cartItem.product_owner_id,
                cartItem.title,
                cartItem.price,
                cartItem.cart_quantity,
                cartItem.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address,
            )

            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCT)
                .document(cartItem.product_id!!)

            writeBatch.set(documentReference, soldProduct)

        }

        for( cartItem in cartList){
            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cartItem.id!!)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {
            activity.allDetailsUpdatedSuccessfully()
        }
         .addOnFailureListener {
                e->
             activity.hideProgressDialog()
             Log.e(activity.javaClass.simpleName,"Error while updating all the details after order placed.",e)
            }

    }

    fun getSoldProductsList(fragment: SoldProductsFragment){
        mFireStore.collection(Constants.SOLD_PRODUCT)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document ->
                val list: ArrayList<SoldProduct> = ArrayList()
                for( i in document.documents){
                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id
                    list.add(soldProduct)
            }

                fragment.successSoldProductsList(list)


            }
            .addOnFailureListener {
                e->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName,
                "Error while getting the list of sold products.",
                    e)

            }
    }

    fun getMyOrdersList(fragment: OrdersFragment){
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener {
                    document ->
                val list: ArrayList<Order> = ArrayList()
                for( i in document.documents){
                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id
                    list.add(orderItem)
                }

                fragment.populateOrdersListInUI(list)

            }
            .addOnFailureListener {
                    e->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName,"Error while getting the orders list.", e)
            }
    }
    fun getShopOrdersList(shopOrdersFragment: ShopOrdersFragment) {
        mFireStore.collection(Constants.ORDERS)
            .get()
            .addOnSuccessListener { documents ->
                val orderList: ArrayList<Order> = ArrayList()
                for (document in documents) {
                    val orderItem = document.toObject(Order::class.java)
                    orderItem.id = document.id
                    orderList.add(orderItem)
                }

                shopOrdersFragment.populateOrdersListInUI(orderList)
            }
            .addOnFailureListener { e ->
                shopOrdersFragment.hideProgressDialog()
                Log.e(shopOrdersFragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }



    /*fun getAllOrdersList(fragment: OrdersFragment) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Constants.ORDERS)
            .get()
            .addOnSuccessListener { result ->
                val ordersList: ArrayList<Order> = ArrayList()
                for (document in result) {
                    val order = document.toObject(Order::class.java)
                    order.id = document.id
                    ordersList.add(order)
                }
                fragment.populateOrdersListInUI(ordersList)
            }
            .addOnFailureListener { exception ->
                // Xử lý khi có lỗi xảy ra
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", exception)
            }
    }*/

    fun placeOrder(activity: CheckoutActivity, order: Order){
        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlaceSuccess()

            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
                )
            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String){
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }

    fun  updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String){
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                "Error while updating the Address.",
                e
                )
            }

    }
    fun getAddressesList(activity: AddressListActivity){
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val addressList: ArrayList<Address> = ArrayList()
                for (i in document.documents){
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }
                activity.successAddressListFromFirestore(addressList)

            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting the address list.",e)
            }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address){

        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()

            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                "Error while adding the address.",
                e)
            }

    }

    fun updateMyCart(context: Context,cart_id: String, itemHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {

                when(context){
                    is CartListActivity ->{
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {
                e->
                when(context){
                    is CartListActivity ->{
                        context.hideProgressDialog()
                    }
                }
                Log.e(context.javaClass.simpleName,
                "Error while updating the cart item.",e)
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                if (document.documents.size > 0) {
                    // Sản phẩm đã tồn tại trong giỏ hàng
                    activity.productExistsInCart()
                } else {
                    // Sản phẩm chưa tồn tại trong giỏ hàng
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while checking the existing cart list.", e)
            }
    }



    fun getAllProductsList(activity: Activity){
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener {
                document ->
                Log.e("Product List", document.documents.toString())
                val productList: ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productList.add(product)
                }
                when(activity){
                    is CartListActivity ->{
                        activity.successProductsListFromFireStore(productList)
                    }
                    is CheckoutActivity ->{
                        activity.successProductsListFromFireStore(productList)
                    }
                }


            }
            .addOnFailureListener {
                e->
                when(activity){
                    is CartListActivity ->{
                        activity.hideProgressDialog()
                    }

                }

                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }

    fun removeItemFromCart(context: Context, cart_id: String){
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when(context){
                    is CartListActivity ->{
                        context.itemRemoveSuccess()
                    }
                }
            }
            .addOnFailureListener {
                e ->
                when(context){
                    is CartListActivity ->{
                        context.hideProgressDialog()
                    }
                }
                Log.e(context.javaClass.simpleName,
                "Error while removing the item from the cart list.",
                e)
            }

    }

    fun getDashboardItemsList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)

            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                fragment.successDashboardItemsList(productsList)

            }
            .addOnFailureListener {
                e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName,"Error while getting dashboard items list.", e)

            }
    }



}



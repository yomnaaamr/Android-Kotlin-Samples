package com.example.bookreview.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookreview.data.Book
import com.example.bookreview.data.BookDao
import kotlinx.coroutines.launch

class BookViewModel(private val bookDao: BookDao) : ViewModel() {

    val allBooks : LiveData<List<Book>> = bookDao.getBooks().asLiveData()

    private fun insertBook(book: Book){
        viewModelScope.launch {
            bookDao.insert(book)
        }
    }


    private fun getNewBookEntry(
        bookName: String,
        bookAuthor:String,
        bookNumPage: String,
        bookReview:String,
        bookRate:Int) : Book{
        return Book(
            bookName = bookName,
            bookAuthor = bookAuthor,
            bookNumPage = bookNumPage,
            bookReview = bookReview,
            bookRate = bookRate
        )
    }

    fun addNewBook(
        bookName: String,
        bookAuthor:String,
        bookNumPage: String,
        bookReview:String,
        bookRate:Int){
        val newBook = getNewBookEntry(bookName,bookAuthor,bookNumPage,bookReview,bookRate)
        insertBook(newBook)
    }


    fun isEntryValid(bookName: String, bookAuthor:String, bookNumPage: String, bookReview:String,bookRate: Int) :Boolean{
        if(bookName.isBlank() || bookAuthor.isBlank() || bookNumPage.isBlank() || bookReview.isBlank() || bookRate.toString().isBlank()){
            return false
        }
        return true
    }

    fun retrieveBook(id : Int) : LiveData<Book>{
        return bookDao.getBook(id).asLiveData()
    }


    fun deleteBook(book: Book){
        viewModelScope.launch {
            bookDao.delete(book)
        }
    }

    private fun updateBook(book: Book){
        viewModelScope.launch {
            bookDao.update(book)
        }
    }


    private fun getUpdatedBookEntry(
        bookId : Int,
        bookName: String,
        bookAuthor:String,
        bookNumPage: String,
        bookReview:String,
        bookRate:Int
    ) : Book{
        return Book(
            id = bookId,
            bookName = bookName,
            bookAuthor = bookAuthor,
            bookNumPage = bookNumPage,
            bookReview = bookReview,
            bookRate = bookRate
        )
    }

    fun updateBook(
        bookId : Int,
        bookName: String,
        bookAuthor:String,
        bookNumPage: String,
        bookReview:String,
        bookRate:Int
    ){
        val updatedBook = getUpdatedBookEntry(bookId,bookName,bookAuthor,bookNumPage,bookReview,bookRate)
        updateBook(updatedBook)

    }

}


class BookReviewViewModelFactory(private val bookDao: BookDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(bookDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
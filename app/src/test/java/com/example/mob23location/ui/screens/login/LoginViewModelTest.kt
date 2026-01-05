package com.example.mob23location.ui.screens.login

import com.example.mob23location.data.repo.IUserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginViewModelTest {
    val repo: IUserRepo = mock()
    val viewModel = LoginViewModel(repo)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        whenever(repo.getUser()).thenReturn("Khayrul")
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchUser returns the mock value`() {
        assertEquals(expected = "Khayrul", viewModel.fetchUser())
    }

    @Test
    fun `greetings should return hello the mocked_value`() {
        assertEquals(expected = "Hello Khayrul", viewModel.greetings())
    }

    @Test
    fun `fetchUser should update the greetings stateflow with greetings`() = runTest{
        viewModel.fetchUser()
        val greetings = viewModel.greetings.first()
        assertEquals("Hello Khayrul", greetings)
    }

    @Test
    fun `greet function should update the greetings stateflow with Hello $name`() = runTest {
        viewModel.greet("Khayrul")
        val msg = viewModel.greetings.drop(1).first()
        assertEquals("Hello Khayrul", msg, "Extra info about test")
    }

    // For login
    @Test
    fun `login function should update the finish sharedFlow with a Unit`() = runTest {
        viewModel.login("email@a.com", "password")
        val result = viewModel.finish.first()
        assertEquals(Unit, result)
    }

    // For validation
    @Test
    fun `Validation should fail for email and password`() {
        assert(viewModel.validate("email", "password") != null)
    }

    @Test
    fun `Validation should fail for email@a,com and pass`() {
//        runBlocking {  } -> don't use
        assert(viewModel.validate("email@a.com", "pass") != null)
    }

    @Test
    fun `Validation should pass for email@a,com and password`() {
        assert(viewModel.validate("email@a.com", "password") == null)
    }

    //should fail, email in viewmodel is email@a.com
    @Test
    fun `Validation should pass for email@gmail,com and password`() {
        assert(viewModel.validate("email@gmail.com", "password") != null)
    }
}
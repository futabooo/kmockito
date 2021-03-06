package com.sys1yagi.kmockito

import org.mockito.Answers
import org.mockito.MockSettings
import org.mockito.Mockito
import org.mockito.internal.creation.MockSettingsImpl
import org.mockito.internal.util.MockUtil
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.stubbing.OngoingStubbing
import org.mockito.verification.VerificationMode
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

//// Wrappers

// mock

fun <T> Class<T>.mock() = Mockito.mock(this)
fun <T> Class<T>.mock(mockSettings: MockSettings) = Mockito.mock(this, mockSettings)
fun <T> Class<T>.mock(defaultAnswer: Answer<Any>) = Mockito.mock(this, defaultAnswer)
inline fun <reified T : Any> mock() = Mockito.mock(T::class.java)
inline fun <reified T : Any> mock(mockSettings: MockSettings) = Mockito.mock(T::class.java, mockSettings)
inline fun <reified T : Any> mock(defaultAnswer: Answer<Any>) = Mockito.mock(T::class.java, defaultAnswer)

fun <T> T.verify(): T = Mockito.verify(this)
fun <T> T.verify(mode: VerificationMode): T = Mockito.verify(this, mode)
fun <T> T.invoked() = Mockito.`when`(this)

val <T> T.invoked: OngoingStubbing<T>
    get() = invoked()

fun <T> T.doNothing() = Mockito.doNothing().`when`(this)

val <T> T.doNothing: T
    get() = doNothing()

fun <T> T.doCallRealMethod() = Mockito.doCallRealMethod().`when`(this)


val <T> T.doCallRealMethod: T
    get() = doCallRealMethod()

fun <T> T.doAnswer(answer: Answer<*>) = Mockito.doAnswer(answer).`when`(this)

fun <T> T.doAnswer(answer: (InvocationOnMock) -> Any) = Mockito.doAnswer(answer).`when`(this)

// spy

fun <T> T.spy(): T = Mockito.spy(this)
fun <T> T.doReturn(value: Any): T {
    return Mockito.doReturn(value).`when`(this)
}

// matcher

inline fun <reified T : Any> any() = Mockito.any(T::class.java) ?: instance(T::class)
fun <T : Any> instance(kClass: KClass<T>) =
        when {
            kClass.mockable() -> kClass.java.defaultMock()
            else -> kClass.constructors.sortedBy { it.parameters.size }.first().call()
        }

private fun KClass<*>.mockable(): Boolean = !Modifier.isFinal(java.modifiers)
private fun <T> Class<T>.defaultMock(): T {
    val mockSettingsImpl = MockSettingsImpl<T>()
    mockSettingsImpl.defaultAnswer(Answers.RETURNS_DEFAULTS)
    val mockCreationSettings = mockSettingsImpl.confirm(this)
    return MockUtil.createMock(mockCreationSettings)
}

// answer

inline fun <reified T : Any> InvocationOnMock.getArgumentAt(index: Int): T = getArgument(index)
inline fun <reified A> InvocationOnMock.arguments(): A = arguments[0] as A
inline fun <reified A, reified B> InvocationOnMock.arguments2() = Pair(arguments[0] as A, arguments[1] as B)
inline fun <reified A, reified B, reified C> InvocationOnMock.arguments3() = Triple(arguments[0] as A, arguments[1] as B, arguments[2] as C)
inline fun <reified A, reified B, reified C, reified D> InvocationOnMock.arguments4() = Quadruple(arguments[0] as A, arguments[1] as B, arguments[2] as C, arguments[3] as D)
inline fun <reified A, reified B, reified C, reified D, reified E> InvocationOnMock.arguments5() = Quintuple(arguments[0] as A, arguments[1] as B, arguments[2] as C, arguments[3] as D, arguments[4] as E)

// DSL

fun <T> Class<T>.deepMock(): T = Mockito.mock(this, Mockito.RETURNS_DEEP_STUBS)


package ru.lanjusto.busscheduler.common.utils;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Вспомогательный класс для проверки предусловий.
 * <p/>
 * Призван заменить код вроде
 * <pre>
 *   if (parameter == null) {
 *     throw new AssertException("parameter must not be null");
 *   }
 * </pre>
 * более компактным
 * <pre>
 *   Assert.notNull(parameter, "parameter must not be null");
 * </pre>
 * Название класса и методов выбраны так, чтобы они дополняли друг друга,
 * это идея (и, частично, сами названия) заимствована у Spring Framework
 * (org.springframework.util.Assert).
 */
public class Assert {

    /**
     * Удоставеряется, что переданный объект не null,
     * и в противном случае, кидает AssertException.
     * <p/>
     * Позвращает проверяемый объект в неизменном виде,
     * для удобства использования в выражениях.
     *
     * @param testee проверяемый объект
     * @return проверяемый объект, если он не null, в неизменном виде
     * @throws AssertException если проверяемый объект == null
     */
    public static <T> T notNull(T testee) throws AssertException {
        return notNull(testee, "assertion failed: testee must not be null");
    }

    /**
     * Удоставеряется, что переданный объект не null,
     * и в противном случае, кидает AssertException.
     * <p/>
     * Позвращает проверяемый объект в неизменном виде,
     * для удобства использования в выражениях.
     *
     * @param testee проверяемый объект
     * @param claim  утверждение, для отладки/документирования, будет помещено в исключение,
     *               если пооверяемый объект == null
     * @return проверяемый объект, если он не null, в неизменном виде
     * @throws AssertException если проверяемый объект == null
     */
    public static <T> T notNull(T testee, String claim) throws AssertException {
        if (testee == null) {
            throw new AssertException(claim);
        }
        return testee;
    }

    /**
     * Удоставеряется, что переданная строка не <c>null</c> и не пуста.
     * <p/>
     * Позвращает проверяемый объект в неизменном виде,
     * для удобства использования в выражениях.
     *
     * @param testee проверяемая строка
     * @return проверяемая строка, если она не null и не пустая, в неизменном виде
     * @throws AssertException если проверяемая строка == null или == ""
     */
    public static String notNullAndNotEmpty(String testee) throws AssertException {
        isFalse(Strings.isNullOrEmpty(testee), "assertion failed: testee must not neither null neither empty");
        return testee;
    }

    public static void isNotEmpty(Collection collection) throws AssertException {
        isFalse(collection.isEmpty(), "Collection must be not empty");
    }

    public static void isEmpty(Collection collection) throws AssertException {
        isTrue(collection.isEmpty(), "Collection must be empty");
    }

    /**
     * Кидает AssertException.
     *
     * @throws AssertException если вырыжение истинно
     */
    public static void fail() throws AssertException {
        isTrue(false, "assertion failed.");
    }

    /**
     * Удостоверяется, что переданное выражение является ложью,
     * и в противном случае, кидает AssertException.
     *
     * @param test проверяемое выражение
     * @throws AssertException если вырыжение истинно
     */
    public static void isFalse(boolean test) throws AssertException {
        isFalse(test, "assertion failed: test expression must be false");
    }

    /**
     * Удостоверяется, что переданное выражение является ложью,
     * и в противном случае, кидает AssertException.
     *
     * @param test  проверяемое выражение
     * @param claim утверждение, для отладки/документирования, будет помещено в исключение,
     *              если проверяемое выражение истинно
     * @throws AssertException если вырыжение истинно
     */
    public static void isFalse(boolean test, String claim) throws AssertException {
        isTrue(!test, claim);
    }

    /**
     * Удостоверяется, что переданное выражение является истиной,
     * и в противном случае, кидает AssertException.
     *
     * @param test проверяемое выражение
     * @throws AssertException если вырыжение ложно
     */
    public static void isTrue(boolean test) throws AssertException {
        isTrue(test, "assertion failed: test expression must be true");
    }

    /**
     * Удостоверяется, что переданное выражение является истиной,
     * и в противном случае, кидает AssertException.
     *
     * @param test  проверяемое выражение
     * @param claim утверждение, для отладки/документирования, будет помещено в исключение,
     *              если проверяемое выражение ложно
     * @throws AssertException если выражение ложно
     */
    public static void isTrue(boolean test, String claim) throws AssertException {
        if (!test) {
            throw new AssertException(claim);
        }
    }

    /**
     * Удостоверяется, что число имеет точность два знака после запятой.
     *
     * @param value первый объект
     * @throws AssertException если точность не два знакак после запятой
     */
    public static void scaleIs2(BigDecimal value) throws AssertException {
        equals(2, value.scale(), "Number "+ value + " should have 2 digits after decimal point.");
    }

    /**
     * Удостоверяется, что переданные объекты равны (в смысле метода equals()). В противном случае
     * кидает #AssertException
     *
     * @param o1 первый объект
     * @param o2 второй объект
     * @throws AssertException если объекты не равны
     */
    public static void equals(Object o1, Object o2) throws AssertException {
        equals(o1, o2, "Assertion failed: objects must be equal");
    }


    /**
     * Удостоверяется, что переданные объекты равны (в смысле метода equals()). В противном случае
     * кидает #AssertException
     *
     * @param o1    первый объект
     * @param o2    второй объект
     * @param claim утверждение, которое будет помещено в исключение
     * @throws AssertException если объекты не равны
     */
    public static void equals(Object o1, Object o2, String claim) throws AssertException {
        if (!o1.equals(o2)) {
            throw new AssertException(claim);
        }
    }
}

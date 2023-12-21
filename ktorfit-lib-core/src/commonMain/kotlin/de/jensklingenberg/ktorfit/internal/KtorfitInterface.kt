package de.jensklingenberg.ktorfit.internal

public interface KtorfitInterface{
    public var _converter: KtorfitConverterHelper
}

public class Default : KtorfitInterface{
    override lateinit var _converter: KtorfitConverterHelper}
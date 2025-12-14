package edu.stanford.spezi.core

/**
 * Defines the key module that orchestrates the data flow in the application by meeting requirements defined by modules.
 */
interface Standard : Module

/**
 * The default implementation of [Standard] that does not add any additional functionality.
 */
object DefaultStandard : Standard

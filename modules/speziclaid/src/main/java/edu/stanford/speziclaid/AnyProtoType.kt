package edu.stanford.speziclaid


import adamma.c4dhi.claid.CLAIDANY
import com.google.protobuf.Message

class AnyProtoType {
    private var message: Message

    constructor() {
        // If a Module subscribes/publishes a Channel using AnyProtoType,
        // then during the initialization of the Module, an example DataPackage will be created,
        // which is used to tell the Middleware what data is expected by the Module.
        // Hence, during publish/subscribe, a Mutator will be created for AnyProtoType, which will be used
        // to set an example instance for a DataPackage.
        // For this to work, message cannot be null.
        // Hence, in the case of the default constructor, we have to initialize message with a valid proto type.
        // The type itself does not matter, because all proto types are represented by DataPackage::PayloadOneOfCase::kBlobVal.
        // Therefore, we choose NumberArray here, because it is a small data type with only minimal overhead.
        message = CLAIDANY.getDefaultInstance()
    }

    constructor(message: Message) {
        this.message = message
    }

    fun setMessage(message: Message) {
        this.message = message
    }

    fun getMessage(): Message {
        return this.message
    }
}

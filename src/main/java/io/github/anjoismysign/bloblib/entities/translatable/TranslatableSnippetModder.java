package io.github.anjoismysign.bloblib.entities.translatable;

public class TranslatableSnippetModder extends BlobTranslatableModder<TranslatableSnippet, String> {

    /**
     * Will create a new instance of BlobTranslatableModder.
     *
     * @param translatable The translatable to modify
     * @return The BlobTranslatableModder
     */
    public static TranslatableSnippetModder mod(TranslatableSnippet translatable) {
        TranslatableSnippetModder translatableModder = new TranslatableSnippetModder();
        translatableModder.translatable = translatable;
        return translatableModder;
    }
}

package us.mytheria.bloblib.entities.translatable;

public interface TranslatableSnippet extends Translatable<String> {
    default TranslatableSnippetModder modder() {
        return TranslatableSnippetModder.mod(this);
    }
}

package eu.neosurance.sdk.interactors;

public interface DefaultUseCase<T> {
    void execute(T e);
}

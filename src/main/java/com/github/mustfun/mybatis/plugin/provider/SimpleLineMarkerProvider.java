package com.github.mustfun.mybatis.plugin.provider;

import com.google.common.base.Optional;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 * @update itar
 * @function 自定义lineMarkerProvider ， 从F跳转到T
 */
public abstract class SimpleLineMarkerProvider<F extends PsiElement, T> extends MarkerProviderAdaptor {

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        boolean naviOpenStatus = PropertiesComponent.getInstance().getBoolean("naviOpenStatus");
        if (!naviOpenStatus) {
            return null;
        }
        //检测是不是需要标记的元素
        if (!isTheElement(element)) {
            return null;
        }

        //Psi对象转化为T对象
        Optional<T> processResult = apply((F) element);
        return processResult.isPresent() ? new LineMarkerInfo<F>(
            (F) element,
            element.getTextRange(),
            getIcon(),
            getTooltipProvider(processResult.get()),
            getNavigationHandler(processResult.get()),
            GutterIconRenderer.Alignment.CENTER
        ) : null;
    }

    private Function<F, String> getTooltipProvider(final T target) {
        return new Function<F, String>() {
            @Override
            public String fun(F from) {
                return getTooltip(from, target);
            }
        };
    }

    private GutterIconNavigationHandler<F> getNavigationHandler(final T target) {
        return new GutterIconNavigationHandler<F>() {
            @Override
            public void navigate(MouseEvent e, F from) {
                getNavigatable(from, target).navigate(true);
            }
        };
    }

    public abstract boolean isTheElement(@NotNull PsiElement element);

    @NotNull
    public abstract Optional<T> apply(@NotNull F from);

    @NotNull
    public abstract Navigatable getNavigatable(@NotNull F from, @NotNull T target);

    @NotNull
    public abstract String getTooltip(@NotNull F from, @NotNull T target);

    @NotNull
    public abstract Icon getIcon();
}

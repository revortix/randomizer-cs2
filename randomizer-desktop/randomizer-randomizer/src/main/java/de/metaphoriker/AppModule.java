package de.metaphoriker;

import com.google.inject.AbstractModule;
import de.metaphoriker.view.viewmodel.BuilderViewModel;
import de.metaphoriker.view.views.BuilderView;
import de.metaphoriker.view.views.MainWindow;
import de.metaphoriker.view.views.RandomizerView;
import de.metaphoriker.view.views.SidebarView;

public class AppModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(BuilderViewModel.class).asEagerSingleton();
    bind(BuilderView.class);
    bind(MainWindow.class);
    bind(RandomizerView.class);
    bind(SidebarView.class);
  }
}
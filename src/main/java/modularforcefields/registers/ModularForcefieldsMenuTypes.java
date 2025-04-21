package modularforcefields.registers;

import modularforcefields.ModularForcefields;
import modularforcefields.common.inventory.container.ContainerBiometricIdentifier;
import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModularForcefieldsMenuTypes {
	
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, ModularForcefields.ID);

	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCoercionDeriver>> CONTAINER_COERCIONDERIVER = register("coercionderiver", ContainerCoercionDeriver::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerFortronCapacitor>> CONTAINER_FORTRONCAPACITOR = register("fortroncapacitor", ContainerFortronCapacitor::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerFortronFieldProjector>> CONTAINER_FORTRONFIELDPROJECTOR = register("fortronfieldprojector", ContainerFortronFieldProjector::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerInterdictionMatrix>> CONTAINER_INTERDICTIONMATRIX = register("interdictionmatrix", ContainerInterdictionMatrix::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerBiometricIdentifier>> CONTAINER_BIOMETRICIDENTIFIER = register("biometricidentifier", ContainerBiometricIdentifier::new);

	private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String id, MenuSupplier<T> supplier) {
		return MENU_TYPES.register(id, () -> new MenuType<>(supplier, FeatureFlags.VANILLA_SET));
	}
}

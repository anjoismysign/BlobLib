package io.github.anjoismysign.bloblib.entities;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.Locale;

public class AttributeModifierBean {
        private String key;
        private AttributeModifier.Operation operation;
        private double amount;
        private String equipmentSlotGroup;

        public static AttributeModifierBean ofAttributeModifier(AttributeModifier attributeModifier){
                AttributeModifierBean bean = new AttributeModifierBean();
                bean.setKey(attributeModifier.getKey().toString());
                bean.setOperation(attributeModifier.getOperation());
                bean.setAmount(attributeModifier.getAmount());
                bean.setEquipmentSlotGroup(attributeModifier.getSlotGroup().toString());
                return bean;
        }

        public AttributeModifier toAttributeModifier(){
                Key key = Key.key(this.key);
                NamespacedKey namespacedKey = new NamespacedKey(key.namespace(), key.value());
                return new AttributeModifier(namespacedKey, amount, operation, EquipmentSlotGroup.getByName(equipmentSlotGroup.toLowerCase(Locale.ROOT)));
        }

        public String getKey() {
                return key;
        }

        public void setKey(String key) {
                this.key = key;
        }

        public AttributeModifier.Operation getOperation() {
                return operation;
        }

        public void setOperation(AttributeModifier.Operation operation) {
                this.operation = operation;
        }

        public double getAmount() {
                return amount;
        }

        public void setAmount(double amount) {
                this.amount = amount;
        }

        public String getEquipmentSlotGroup() {
                return equipmentSlotGroup;
        }

        public void setEquipmentSlotGroup(String equipmentSlotGroup) {
                this.equipmentSlotGroup = equipmentSlotGroup;
        }
}
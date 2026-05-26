package io.github.anjoismysign.bloblib.entities;

import org.bukkit.attribute.AttributeModifier;

public class AttributeModifierBean {
        private double amount;
        private AttributeModifier.Operation operation;
        private String attribute;

        public double getAmount() {
                return amount;
        }

        public void setAmount(double amount) {
                this.amount = amount;
        }

        public AttributeModifier.Operation getOperation() {
                return operation;
        }

        public void setOperation(AttributeModifier.Operation operation) {
                this.operation = operation;
        }

        public String getAttribute() {
                return attribute;
        }

        public void setAttribute(String attribute) {
                this.attribute = attribute;
        }
}
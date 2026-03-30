package com.blindaje.modules.visit.Dto;

import java.util.List;

public class CompanionRequest {

    private List<CompanionItem> companions;

    public List<CompanionItem> getCompanions() { return companions; }

    public static class CompanionItem {
        private String name;
        private String document;

        public String getName() { return name; }
        public String getDocument() { return document; }
    }
}
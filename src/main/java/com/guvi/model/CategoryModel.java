    package com.guvi.model;

    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;

    @Document(collection = "categories")
    public class CategoryModel {
        @Id
        private String id;
        private String name;
        private Boolean active;

        public CategoryModel(String id, String name, Boolean active) {
            this.id = id;
            this.name = name;
            this.active = active;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

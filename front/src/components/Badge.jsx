import { Tag } from "primereact/tag";

export const getActiveBadge = (isActive) => {
    return (
        <Tag severity={isActive ? "success" : "danger"} value={isActive ? "Active" : "Inactive"} />
    );
};

export const getPlainBadge = (value) => {
    return (
        <Tag value={value} />
    );
};
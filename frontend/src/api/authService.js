import apiClient from "./apiClient";

export const login = async (email, password) => {
  try {
    const response = await apiClient.post("/users/login", { email, password });

    const { accessToken, tokenType } = response.data;
    localStorage.setItem("jwtToken", `${tokenType} ${accessToken}`);
    return response;
  } catch (error) {
    console.error("Login failed:", error);
    throw error;
  }
};

export const register = async (email, password) => {
  try {
    const response = await apiClient.post("/users/register", {
      email,
      password,
    });
    return response;
  } catch (error) {
    console.error("Registration failed:", error);
    throw error;
  }
};

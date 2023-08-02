export async function updateUser(axios, firstName, lastName, email)
{
    return await axios.put(`api/Gateway/user`,
        {
            firstName,
            lastName,
            email
        });
}